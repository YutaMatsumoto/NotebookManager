/*
 * Created on Jul 12, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.mysql.jdbc.jdbc2.optional;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.ConnectionEventListener;
import javax.sql.XAConnection;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/*XA BEGIN <xid> [JOIN | RESUME]
XA START TRANSACTION <xid> [JOIN | RESUME]
XA COMMIT <xid> [ONE PHASE]
XA END <xid> [SUSPEND [FOR MIGRATE]]
XA PREPARE <xid>
XA RECOVER
XA ROLLBACK <xid>
*/

/**
 * An object that provides support for distributed
 * transactions.  An <code>XAConnection</code> object  may be enlisted
 * in a distributed transaction by means of an <code>XAResource</code> object.
 * A transaction manager, usually part of a middle tier server, manages an
 * <code>XAConnection</code> object through the <code>XAResource</code> object.
 * <P>
 * An application programmer does not use this interface directly; rather,
 * it is used by a transaction manager working in the middle tier server.
 *
 * @since 1.4
 */
public class MysqlXAConnection extends MysqlPooledConnection
    implements XAConnection, XAResource {
	
	private Connection underlyingConnection;
	
    /**
     * @param connection
     */
    public MysqlXAConnection(Connection connection) {
        super(connection);
        this.underlyingConnection = connection;
    }

    /**
     * Retrieves an <code>XAResource</code> object that
     * the transaction manager will use
     * to manage this <code>XAConnection</code> object's participation in a
     * distributed transaction.
     *
     * @return the <code>XAResource</code> object
     * @exception SQLException if a database access error occurs
     */
    public XAResource getXAResource() throws SQLException {
       return this;
    }

    /**
     * Obtains the current transaction timeout value set for this
     * XAResource instance. If XAResource.setTransactionTimeout  was not used
     * prior to invoking this method, the return value is the default timeout set
     * for the resource manager; otherwise, the value used in the previous
     * setTransactionTimeout  call is returned.
     *
     * @return the transaction timeout value in seconds.
     *
     * @throws XAException An error has occurred. Possible exception values
     * are XAER_RMERR and XAER_RMFAIL.
     */
    public int getTransactionTimeout() throws XAException {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * Sets the current transaction timeout value for this XAResource
     * instance. Once set, this timeout value is effective until
     * setTransactionTimeout is invoked again with a different value.
     *
     * To reset the timeout value to the default value used by the
     * resource manager, set the value to zero. If the timeout operation
     * is performed successfully, the method returns true; otherwise false.
     *
     * If a resource manager does not support explicitly setting the
     * transaction timeout value, this method returns false.
     *
     * @parameter seconds The transaction timeout value in seconds.
     *
     * @return true if the transaction timeout value is set successfully; otherwise false.
     *
     * @throws XAException An error has occurred. Possible exception values
     *  are XAER_RMERR, XAER_RMFAIL, or XAER_INVAL.
     */
    public boolean setTransactionTimeout(int arg0) throws XAException {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * This method is called to determine if the resource manager
     * instance represented by the target object is the same as the
     * resouce manager instance represented by the parameter xares.
     *
     * @parameter xares An XAResource object whose resource manager
     * instance is to be compared with the resource manager instance
     * of the target object.
     *
     * @return true if it's the same RM instance; otherwise false.
     *
     * @throws XAException An error has occurred. Possible exception
     * values are XAER_RMERR and XAER_RMFAIL.
     */
    public boolean isSameRM(XAResource xares) throws XAException {
        
        return (xares == this);
    }

    /**
     * Obtains a list of prepared transaction branches from a resource
     * manager. The transaction manager calls this method during
     * recovery to obtain the list of transaction branches that are
     * currently in prepared or heuristically completed states.
     *
     * @param flag One of TMSTARTRSCAN, TMENDRSCAN, TMNOFLAGS.
     * TMNOFLAGS must be used when no other flags are set in the parameter.
     *
     * @returns The resource manager returns zero or more XIDs of the
     * transaction branches that are currently in a prepared or
     * heuristically completed state. If an error occurs during the
     * operation, the resource manager should throw the appropriate
     * XAException.
     *
     * @throws XAException An error has occurred. Possible values are XAER_RMERR, XAER_RMFAIL, XAER_INVAL, and XAER_PROTO.
     */
    public Xid[] recover(int flag) throws XAException {
        switch (flag) {
        case TMSTARTRSCAN:
        case TMENDRSCAN:
        case TMNOFLAGS:
      
            ResultSet rs = null;
            
            try {
            	rs = dispatchCommand("XA RECOVER");
            
            	if (rs != null) {
            		while (rs.next()) {
            			String gtridBtrid = rs.getString(1);
            		}
            	}
            } catch (SQLException sqlEx) {
            	throw mapXAExceptionFromSQLException(sqlEx);
            } finally {
            	if (rs != null) {
            		try {
            			rs.close();
            		} catch (SQLException sqlEx) {
            			throw mapXAExceptionFromSQLException(sqlEx);
            		}
            	}
            }
            
            return new Xid[0];
        default:
            throw new XAException(XAException.XAER_INVAL);
        }
    }

    /**
     * Asks the resource manager to prepare for a transaction commit 
     * of the transaction specified in xid.
     *
     * @parameter xid A global transaction identifier.
     *
     * @returns A value indicating the resource manager's vote on the 
     * outcome of the transaction. 
     * 
     * The possible values are: XA_RDONLY or XA_OK. If the resource 
     * manager wants to roll back the transaction, it should do so by 
     * raising an appropriate XAException in the prepare method.
     * 
     * @throws XAException An error has occurred. Possible exception 
     * values are: XA_RB*, XAER_RMERR, XAER_RMFAIL, XAER_NOTA, XAER_INVAL, 
     * or XAER_PROTO.
     */
    public int prepare(Xid xid) throws XAException {
		StringBuffer commandBuf = new StringBuffer();
    	commandBuf.append("XA PREPARE ");
    	commandBuf.append(xidToString(xid));
    	
    	dispatchCommand(commandBuf.toString());
    	
    	return XA_OK; // TODO: Check for read-only
    }

    /**
     * Tells the resource manager to forget about a heuristically completed 
     * transaction branch.
     *
     * @parameter xid A global transaction identifier.
     *
     * @throws XAException An error has occurred. Possible exception values 
     * are XAER_RMERR, XAER_RMFAIL, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
     */
    public void forget(Xid xid) throws XAException {
        // TODO Auto-generated method stub
    }

    /**
     * Informs the resource manager to roll back work done on behalf of a 
     * transaction branch.
     *
     * @parameter xid A global transaction identifier.
     *
     * @throws XAException An error has occurred. Possible XAExceptions are 
     * XA_HEURHAZ, XA_HEURCOM, XA_HEURRB, XA_HEURMIX, XAER_RMERR, 
     * XAER_RMFAIL, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
     *
     * If the transaction branch is already marked rollback-only the 
     * resource manager may throw one of the XA_RB* exceptions. 
     * 
     * Upon return, the resource manager has rolled back the branch's work 
     * and has released all held resources.
     */
    public void rollback(Xid xid) throws XAException {
    	StringBuffer commandBuf = new StringBuffer();
    	commandBuf.append("XA ROLLBACK ");
    	commandBuf.append(xidToString(xid));
    	
    	dispatchCommand(commandBuf.toString());
    }

    /**
     * Ends the work performed on behalf of a transaction branch. 
     * 
     * The resource manager disassociates the XA resource from the 
     * transaction branch specified and lets the transaction complete.
     * 
     * If TMSUSPEND is specified in the flags, the transaction branch 
     * is temporarily suspended in an incomplete state. The transaction 
     * context is in a suspended state and must be resumed via the 
     * start method with TMRESUME specified.
     * 
     * If TMFAIL is specified, the portion of work has failed. The 
     * resource manager may mark the transaction as rollback-only
     * 
     * If TMSUCCESS is specified, the portion of work has completed 
     * successfully.
     * 
     * @parameter xid A global transaction identifier that is the same 
     * as the identifier used previously in the start method.
     * 
     * @parameter flags One of TMSUCCESS, TMFAIL, or TMSUSPEND.
     *
     * @throws XAException - An error has occurred. Possible XAException 
     * values are XAER_RMERR, XAER_RMFAIL, XAER_NOTA, XAER_INVAL, XAER_PROTO, 
     * or XA_RB*.
     */
    public void end(Xid xid, int flags) throws XAException {
    	StringBuffer commandBuf = new StringBuffer();
        commandBuf.append("XA END ");
        commandBuf.append(xidToString(xid));
        
        switch (flags) {
        case TMSUCCESS:
        	break; // no-op
        case TMSUSPEND:
        	commandBuf.append(" SUSPEND");
        	break;
        case TMFAIL:
        	break; // no-op
        default:
        	throw new XAException(XAException.XAER_INVAL);
        }
    	
        dispatchCommand(commandBuf.toString());
    }

    /**
     * Starts work on behalf of a transaction branch specified in xid. 
     * 
     * If TMJOIN is specified, the start applies to joining a 
     * transaction previously seen by the resource manager. 
     * 
     * If TMRESUME is specified, the start applies to resuming a 
     * suspended transaction specified in the parameter xid. 
     * 
     * If neither TMJOIN nor TMRESUME is specified and the transaction 
     * specified by xid has previously been seen by the resource manager, 
     * the resource manager throws the XAException exception with 
     * XAER_DUPID error code.
     * 
     * @parameter xid A global transaction identifier to be associated 
     * with the resource.
     * 
     * @parameter flags One of TMNOFLAGS, TMJOIN, or TMRESUME.
     * 
     * @throws XAException An error has occurred. Possible exceptions are 
     * XA_RB*, XAER_RMERR, XAER_RMFAIL, XAER_DUPID, XAER_OUTSIDE, XAER_NOTA, 
     * XAER_INVAL, or XAER_PROTO.
     */
    public void start(Xid xid, int flags) throws XAException {
        StringBuffer commandBuf = new StringBuffer();
        commandBuf.append("XA START TRANSACTION ");
        commandBuf.append(xidToString(xid));
        
        switch (flags) {
        case TMJOIN:
        	commandBuf.append(" JOIN");
        	break;
        case TMRESUME:
        	commandBuf.append(" RESUME");
        	break;
        case TMNOFLAGS:
        		// no-op
        	break;
        default:
        	throw new XAException(XAException.XAER_INVAL);
        }
    	
        dispatchCommand(commandBuf.toString());
        
    }

    /**
     * Commits the global transaction specified by xid.
     *
     * @parameter xid A global transaction identifier
     * @parameter onePhase - If true, the resource manager should 
     * use a one-phase commit protocol to commit the work done on 
     * behalf of xid.
     *
     * @throws XAException An error has occurred. Possible XAExceptions 
     * are XA_HEURHAZ, XA_HEURCOM, XA_HEURRB, XA_HEURMIX, XAER_RMERR, 
     * XAER_RMFAIL, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
     *
     * If the resource manager did not commit the transaction and the 
     * parameter onePhase is set to true, the resource manager may throw 
     * one of the XA_RB* exceptions. 
     * 
     * Upon return, the resource manager has rolled back the 
     * branch's work and has released all held resources.
     */

    public void commit(Xid xid, boolean onePhase) throws XAException {
    	StringBuffer commandBuf = new StringBuffer();
    	commandBuf.append("XA COMMIT ");
    	commandBuf.append(xidToString(xid));
    	
    	if (onePhase) {
    		commandBuf.append(" ONE PHASE");
    	}
        
    	dispatchCommand(commandBuf.toString());
    }
    
    private ResultSet dispatchCommand(String command) throws XAException {
    	try {
    		// TODO: Cache this for lifetime of XAConnection
    		Statement stmt = this.underlyingConnection.createStatement();
    		
    		//stmt.execute(command);
    		
    		//return stmt.getResultSet();
    		System.out.println(this + " -> dispatching command: " + command);
    		
    		return null;
    	} catch (SQLException sqlEx) {
    		throw mapXAExceptionFromSQLException(sqlEx);
    	}
    }
    
    private static XAException mapXAExceptionFromSQLException(SQLException sqlEx) {
    	return new XAException(sqlEx.getErrorCode());
    }
    
    private static String xidToString(Xid xid) {
    	byte[] gtrid = xid.getGlobalTransactionId();
    	
    	byte[] btrid = xid.getBranchQualifier();
    	
    	int lengthAsString = 1; // for '.'
    	
    	if (gtrid != null) {
    		lengthAsString += (2 * gtrid.length);
    	}
    	
    	if (btrid != null) {
    		lengthAsString += (2 * btrid.length);
    	}
    	
    	StringBuffer asString = new StringBuffer(lengthAsString);
    	
    	if (gtrid != null) {
    		for (int i = 0; i < gtrid.length; i++) {
    			String asHex = Integer.toHexString(gtrid[i] & 0xff);
    			
    			if (asHex.length() == 1) {
    				asString.append("0");
    			}
    			
    			asString.append(asHex);
    		}
    	}

    	if (btrid != null) {
    		asString.append(".");
    		
    		for (int i = 0; i < btrid.length; i++) {
    			String asHex = Integer.toHexString(btrid[i] & 0xff);
    			
    			if (asHex.length() == 1) {
    				asString.append("0");
    			}
    			
    			asString.append(asHex);
    		}
    	}
    	
    	return asString.toString();
    }
    
	/* (non-Javadoc)
	 * @see javax.sql.PooledConnection#getConnection()
	 */
	public synchronized Connection getConnection() throws SQLException {
		Connection connToWrap = super.getConnection();
		
		return connToWrap;
	}
}
