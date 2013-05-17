JC=javac
JI=java
JFLAGS = -g -Xlint
SRCS = $(shell find *.java)
OBJS = $(SRCS:.java=.class)
MYSQL_CONN=mysql-connector-java-3.2.0-alpha/mysql-connector-java-3.2.0-alpha-bin.jar:.

.SUFFIXES: .java .class

# ---------------------------------------------------------------------------
# Default Rule

default : $(OBJS)
	 java -cp $(MYSQL_CONN) GuiMain

$(OBJS) : $(SRCS)
	$(JC) $(JFLAGS) $(SRCS)

# ---------------------------------------------------------------------------
# Compile

classes: $(CLASSES:.java=.class)

.java.class:
	$(JC) $(JFLAGS) $*.java

# ---------------------------------------------------------------------------
# misc

clean :
	rm *.class tags

tags : $(shell ls *.java)
	ctags -R .
