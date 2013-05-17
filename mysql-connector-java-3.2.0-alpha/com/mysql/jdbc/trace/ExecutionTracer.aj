/*

Copyright (c) Xerox Corporation 1998-2002.  All rights reserved.

Use and copying of this software and preparation of derivative works based
upon this software are permitted.  Any distribution of this software or
derivative works must comply with all applicable United States export control
laws.

This software is made available AS IS, and Xerox Corporation makes no warranty
about the software, its performance or its conformity to any specification.

|<---            this code is formatted to fit into 80 columns             --->|
|<---            this code is formatted to fit into 80 columns             --->|
|<---            this code is formatted to fit into 80 columns             --->|

*/

package com.mysql.jdbc.trace;

import java.io.PrintStream;
import org.aspectj.lang.JoinPoint;


/**
 * This class provides support for printing trace messages into a stream. 
 * The trace messages consist of the class name, method name (if method)
 * and the list of parameter types.<P>
 * The class is thread-safe. Different threads may use different output streams
 * by simply calling the method initStream(myStream).<P>
 * This class should be extended.
 * It defines 3 abstract crosscuts for injecting the tracing functionality 
 * into any constructors and methods of any application classes.<P>
 *
 * One example of using this class might be
 * <PRE>
 * import tracing.lib.AbstractTrace;
 * aspect TraceMyClasses extends AbstractTrace of eachJVM() {
 *   pointcut classes(): within(TwoDShape) | within(Circle) | within(Square);
 *   pointcut constructors(): executions(new(..));
 *   pointcut methods(): executions(!abstract * *(..))
 * }
 * </PRE>
 * (Make sure .../aspectj/examples is in your classpath)
 */
public aspect ExecutionTracer {

	 /**
     * The constructors in those classes - but only the ones with 3
     * arguments.
     */
    pointcut constructors(): execution(* *(..)) && within(com.mysql.jdbc.* ) 
    	&& within (!com.mysql.jdbc.trace.*);
    /**
     * This specifies all the message executions.
     */
    pointcut methods(): execution(* *(..)) && within(com.mysql.jdbc.* ) 
    	&& within (!com.mysql.jdbc.trace.*);

    before(): constructors() && methods() {
	doTraceEntry(thisJoinPoint, false);
    }
    after(): constructors() && methods() {
	doTraceExit(thisJoinPoint,  false);
    }

    /*
     * From here on, it's an ordinary class implementation.
     * The static state is thread-safe by using ThreadLocal variables.
     */

    private ThreadLocal stream = new ThreadLocal() {
	    protected Object initialValue() {
		return System.err;
	    }
	};
    private ThreadLocal callDepth = new ThreadLocal() {
	    protected Object initialValue() {
		return new Integer(0);
	    }
	};

    private PrintStream getStream() { 
	return (PrintStream)stream.get(); 
    }
    private void setStream(PrintStream s) { 
	stream.set(s); 
    }
    private int  getCallDepth() { 
	return ((Integer)(callDepth.get())).intValue();
    }
    private void setCallDepth(int n) { 
	callDepth.set(new Integer(n)); 
    }

    private void doTraceEntry (JoinPoint jp, boolean isConstructor) {
	setCallDepth(getCallDepth() + 1);
	printEntering(jp, isConstructor);
    }

    private void doTraceExit (JoinPoint jp,  boolean isConstructor) {
	printExiting(jp, isConstructor);
	setCallDepth(getCallDepth() - 1);
    }

    private void printEntering (JoinPoint jp, boolean isConstructor) {
	printIndent();
	getStream().print("--> ");
	
	
		 getStream().print(jp.getSignature().getDeclaringTypeName());
		 getStream().print(":" + jp.getSourceLocation().getFileName() + ":" + jp.getSourceLocation().getLine());
         getStream().print("(" + jp.getArgs() + ")");
    printParameters(jp);
	getStream().println();
    }

    private void printExiting (JoinPoint jp, boolean isConstructor) {
	printIndent();
	getStream().print("<--  ");
	getStream().print(jp.toShortString());
	getStream().println();
    }



    private void printIndent() {
		for (int i = 0; i < getCallDepth(); i++)
	    	getStream().print("  ");
    }
    
      private void printParameters(JoinPoint jp) {
  		Object[] params = jp.getArgs();

  		getStream().print("(");
  		for (int i = 0; i < params.length; i++) {
  	    	getStream().print(params[i]);
  	    	if (i < params.length - 1) {
  	    		getStream().print(", ");
  	    	}
  		}
  		getStream().print(")");
      }

}

