/*
      Copyright (C) 2002-2004 MySQL AB

      This program is free software; you can redistribute it and/or modify
      it under the terms of version 2 of the GNU General Public License as 
      published by the Free Software Foundation.

      There are special exceptions to the terms and conditions of the GPL 
      as it is applied to this software. View the full text of the 
      exception in file EXCEPTIONS-CONNECTOR-J in the directory of this 
      software distribution.

      This program is distributed in the hope that it will be useful,
      but WITHOUT ANY WARRANTY; without even the implied warranty of
      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
      GNU General Public License for more details.

      You should have received a copy of the GNU General Public License
      along with this program; if not, write to the Free Software
      Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA



 */
package com.mysql.jdbc;

import com.mysql.jdbc.profiler.ProfileEventSink;
import com.mysql.jdbc.profiler.ProfilerEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import java.math.BigDecimal;

import java.net.URL;

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


/**
 * JDBC Interface for MySQL-4.1 and newer server-side PreparedStatements.
 *
 * @author Mark Matthews
 * @version $Id: ServerPreparedStatement.java,v 1.1.2.48.2.22 2004/12/14 14:29:29 mmatthew Exp $
 */
public class ServerPreparedStatement extends PreparedStatement {
    /* 1 (length) + 2 (year) + 1 (month) + 1 (day) */
    private static final byte MAX_DATE_REP_LENGTH = (byte) 5;

    /*
      1 (length) + 1 (is negative) + 4 (day count) + 1 (hour)
      + 1 (minute) + 1 (seconds) + 4 (microseconds)
    */
    private static final byte MAX_TIME_REP_LENGTH = 13;

    /*
      1 (length) + 2 (year) + 1 (month) + 1 (day) +
      1 (hour) + 1 (minute) + 1 (second) + 4 (microseconds)
    */
    private static final byte MAX_DATETIME_REP_LENGTH = 12;
    private Buffer outByteBuffer;

    /** The Calendar instance used to create date/time bindings */
    private Calendar dateTimeBindingCal = null;

    /** If this statement has been marked invalid, what was the reason? */
    private SQLException invalidationException;

    /** Bind values for individual fields */
    private BindValue[] parameterBindings;

    /** Field-level metadata for parameters */
    private Field[] parameterFields;

    /** Field-level metadata for result sets. */
    private Field[] resultFields;

    /**
     * Flag indicating whether or not the long parameters have been 'switched'
     * back to normal parameters. We can not execute() if clearParameters()
     * hasn't been called in this case.
     */
    private boolean detectedLongParameterSwitch = false;

    /** Has this prepared statement been marked invalid? */
    private boolean invalid = false;

    /** Does this query modify data? */
    private boolean isSelectQuery;

    /** Do we need to send/resend types to the server? */
    private boolean sendTypesToServer = false;

    /**
     * The number of fields in the result set (if any) for this
     * PreparedStatement.
     */
    private int fieldCount;

    /** The type used for string bindings, changes from version-to-version */
    private int stringTypeCode = MysqlDefs.FIELD_TYPE_STRING;

    /** The ID that the server uses to identify this PreparedStatement */
    private long serverStatementId;

    /**
     * Creates a new ServerPreparedStatement object.
     *
     * @param conn the connection creating us.
     * @param sql the SQL containing the statement to prepare.
     * @param catalog the catalog in use when we were created.
     * @param serverStatementId
	 * @param parameterCount
	 * @param fieldCount
	 * @param parameterFields
	 * @param resultFields
     *
     * @throws SQLException If an error occurs
     */

	public ServerPreparedStatement(Connection conn, String catalog, 
			String sql, 
			long serverStatementId, 
			int parameterCount, 
			int fieldCount, 
			Field[] parameterFields, 
			Field[] resultFields)
        throws SQLException {
        super(conn, catalog);

        this.isSelectQuery = StringUtils.startsWithIgnoreCaseAndWs(sql, "SELECT"); //$NON-NLS-1$

        this.useTrueBoolean = this.connection.versionMeetsMinimum(3,
                21, 23);
        this.hasLimitClause = (StringUtils.indexOfIgnoreCase(sql, "LIMIT") != -1); //$NON-NLS-1$
        this.firstCharOfStmt = StringUtils.firstNonWsCharUc(sql);
        this.originalSql = sql;

        if (this.connection.versionMeetsMinimum(4, 1, 2)) {
            this.stringTypeCode = MysqlDefs.FIELD_TYPE_VAR_STRING;
        } else {
            this.stringTypeCode = MysqlDefs.FIELD_TYPE_STRING;
        }

        this.originalSql = sql;
        
        setServersideState(false, serverStatementId, 
        		parameterCount, fieldCount, parameterFields, 
				resultFields);

        this.parameterBindings = new BindValue[parameterCount];
        
        for (int i = 0; i < this.parameterCount; i++) {
        	this.parameterBindings[i] = new BindValue();
        }
    }
	
	protected void setServersideState(boolean forReconnect, 
			long serverStatementId, 
			int parameterCount, 
			int fieldCount, 
			Field[] parameterFields, 
			Field[] resultFields) throws SQLException {
		this.serverStatementId = serverStatementId;
		// FIXME: Check if these have changed on reconnect?
        this.parameterCount = parameterCount;
        this.fieldCount = fieldCount;
        this.parameterFields = parameterFields;
        this.resultFields = resultFields;
	}

    /**
     * @see java.sql.PreparedStatement#setArray(int, java.sql.Array)
     */
    public void setArray(int i, Array x) throws SQLException {
        throw new NotImplemented();
    }

    /**
     * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream,
     *      int)
     */
    public void setAsciiStream(int parameterIndex, InputStream x, int length)
        throws SQLException {
        checkClosed();

        if (x == null) {
            setNull(parameterIndex, java.sql.Types.BINARY);
        } else {
            BindValue binding = getBinding(parameterIndex, true);
            setType(binding, MysqlDefs.FIELD_TYPE_BLOB);

            binding.value = x;
            binding.isNull = false;
            binding.isLongData = true;

            if (this.connection.getUseStreamLengthsInPrepStmts()) {
                binding.bindLength = length;
            } else {
                binding.bindLength = -1;
            }

            this.connection.serverLongData(this, parameterIndex, binding);
        }
    }

    /**
     * @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)
     */
    public void setBigDecimal(int parameterIndex, BigDecimal x)
        throws SQLException {
        checkClosed();

        if (x == null) {
            setNull(parameterIndex, java.sql.Types.DECIMAL);
        } else {
            setString(parameterIndex, fixDecimalExponent(x.toString()));
        }
    }

    /**
     * @see java.sql.PreparedStatement#setBinaryStream(int,
     *      java.io.InputStream, int)
     */
    public void setBinaryStream(int parameterIndex, InputStream x, int length)
        throws SQLException {
        checkClosed();

        if (x == null) {
            setNull(parameterIndex, java.sql.Types.BINARY);
        } else {
            BindValue binding = getBinding(parameterIndex, true);
            setType(binding, MysqlDefs.FIELD_TYPE_BLOB);

            binding.value = x;
            binding.isNull = false;
            binding.isLongData = true;

            if (this.connection.getUseStreamLengthsInPrepStmts()) {
                binding.bindLength = length;
            } else {
                binding.bindLength = -1;
            }

            this.connection.serverLongData(this, parameterIndex, binding);
        }
    }

    /**
     * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
     */
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        checkClosed();

        if (x == null) {
            setNull(parameterIndex, java.sql.Types.BINARY);
        } else {
            BindValue binding = getBinding(parameterIndex, true);
            setType(binding, MysqlDefs.FIELD_TYPE_BLOB);

            binding.value = x;
            binding.isNull = false;
            binding.isLongData = true;

            if (this.connection.getUseStreamLengthsInPrepStmts()) {
                binding.bindLength = x.length();
            } else {
                binding.bindLength = -1;
            }

            this.connection.serverLongData(this, parameterIndex, binding);
        }
    }

    /**
     * @see java.sql.PreparedStatement#setBoolean(int, boolean)
     */
    public void setBoolean(int parameterIndex, boolean x)
        throws SQLException {
        setByte(parameterIndex, (x ? (byte) 1 : (byte) 0));
    }

    /**
     * @see java.sql.PreparedStatement#setByte(int, byte)
     */
    public void setByte(int parameterIndex, byte x) throws SQLException {
        checkClosed();

        BindValue binding = getBinding(parameterIndex, false);
        setType(binding, MysqlDefs.FIELD_TYPE_TINY);

        binding.value = null;
        binding.byteBinding = x;
        binding.isNull = false;
        binding.isLongData = false;
    }

    /**
     * @see java.sql.PreparedStatement#setBytes(int, byte)
     */
    public void setBytes(int parameterIndex, byte[] x)
        throws SQLException {
        checkClosed();

        if (x == null) {
            setNull(parameterIndex, java.sql.Types.BINARY);
        } else {
            BindValue binding = getBinding(parameterIndex, false);
            setType(binding, MysqlDefs.FIELD_TYPE_BLOB);

            binding.value = x;
            binding.isNull = false;
            binding.isLongData = false;
        }
    }

    /**
     * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader,
     *      int)
     */
    public void setCharacterStream(int parameterIndex, Reader reader, int length)
        throws SQLException {
        checkClosed();

        if (reader == null) {
            setNull(parameterIndex, java.sql.Types.BINARY);
        } else {
            BindValue binding = getBinding(parameterIndex, true);
            setType(binding, MysqlDefs.FIELD_TYPE_BLOB);

            binding.value = reader;
            binding.isNull = false;
            binding.isLongData = true;

            if (this.connection.getUseStreamLengthsInPrepStmts()) {
                binding.bindLength = length;
            } else {
                binding.bindLength = -1;
            }

            this.connection.serverLongData(this, parameterIndex, binding);
        }
    }

    /**
     * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
     */
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        checkClosed();

        if (x == null) {
            setNull(parameterIndex, java.sql.Types.BINARY);
        } else {
            BindValue binding = getBinding(parameterIndex, true);
            setType(binding, MysqlDefs.FIELD_TYPE_BLOB);

            binding.value = x.getCharacterStream();
            binding.isNull = false;
            binding.isLongData = true;

            if (this.connection.getUseStreamLengthsInPrepStmts()) {
                binding.bindLength = x.length();
            } else {
                binding.bindLength = -1;
            }

            this.connection.serverLongData(this, parameterIndex, binding);
        }
    }

    /**
     * Set a parameter to a java.sql.Date value.  The driver converts this to a
     * SQL DATE value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @param cal the calendar to interpret the date with
     *
     * @exception SQLException if a database-access error occurs.
     */
    public void setDate(int parameterIndex, Date x, Calendar cal)
        throws SQLException {
        if (x == null) {
            setNull(parameterIndex, java.sql.Types.DATE);
        } else {
            BindValue binding = getBinding(parameterIndex, false);
            setType(binding, MysqlDefs.FIELD_TYPE_DATE);

            binding.value = x;
            binding.isNull = false;
            binding.isLongData = false;
        }
    }

    /**
     * Set a parameter to a java.sql.Date value.  The driver converts this to a
     * SQL DATE value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     *
     * @exception SQLException if a database-access error occurs.
     */
    public void setDate(int parameterIndex, Date x) throws SQLException {
        setDate(parameterIndex, x, null);
    }

    /**
     * @see java.sql.PreparedStatement#setDouble(int, double)
     */
    public void setDouble(int parameterIndex, double x)
        throws SQLException {
        checkClosed();
        
        if (!this.connection.getAllowNanAndInf() && 
    			(x == Double.POSITIVE_INFINITY || x == Double.NEGATIVE_INFINITY ||
    			Double.isNaN(x))) {
    		throw new SQLException("'" + x + 
    				"' is not a valid numeric or approximate numeric value", 
					SQLError.SQL_STATE_ILLEGAL_ARGUMENT);
    		
    	}

        BindValue binding = getBinding(parameterIndex, false);
        setType(binding, MysqlDefs.FIELD_TYPE_DOUBLE);

        binding.value = null;
        binding.doubleBinding = x;
        binding.isNull = false;
        binding.isLongData = false;
    }

    /**
     * @see java.sql.PreparedStatement#setFloat(int, float)
     */
    public void setFloat(int parameterIndex, float x) throws SQLException {
        checkClosed();

        BindValue binding = getBinding(parameterIndex, false);
        setType(binding, MysqlDefs.FIELD_TYPE_FLOAT);

        binding.value = null;
        binding.floatBinding = x;
        binding.isNull = false;
        binding.isLongData = false;
    }

    /**
     * @see java.sql.PreparedStatement#setInt(int, int)
     */
    public void setInt(int parameterIndex, int x) throws SQLException {
        checkClosed();

        BindValue binding = getBinding(parameterIndex, false);
        setType(binding, MysqlDefs.FIELD_TYPE_LONG);

        binding.value = null;
        binding.intBinding = x;
        binding.isNull = false;
        binding.isLongData = false;
    }

    /**
     * @see java.sql.PreparedStatement#setLong(int, long)
     */
    public void setLong(int parameterIndex, long x) throws SQLException {
        checkClosed();

        BindValue binding = getBinding(parameterIndex, false);
        setType(binding, MysqlDefs.FIELD_TYPE_LONGLONG);

        binding.value = null;
        binding.longBinding = x;
        binding.isNull = false;
        binding.isLongData = false;
    }

    /**
     * @see java.sql.PreparedStatement#getMetaData()
     */
    public java.sql.ResultSetMetaData getMetaData() throws SQLException {
        checkClosed();

        return new ResultSetMetaData(this.resultFields);
    }

    /**
     * @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)
     */
    public void setNull(int parameterIndex, int sqlType, String typeName)
        throws SQLException {
        checkClosed();

        BindValue binding = getBinding(parameterIndex, false);

        //
        // Don't re-set types, but use something if this
        // parameter was never specified
        //
        if (binding.bufferType == 0) {
            setType(binding, MysqlDefs.FIELD_TYPE_NULL);
        }

        binding.value = null;
        binding.isNull = true;
        binding.isLongData = false;
    }

    /**
     * @see java.sql.PreparedStatement#setNull(int, int)
     */
    public void setNull(int parameterIndex, int sqlType)
        throws SQLException {
        checkClosed();

        BindValue binding = getBinding(parameterIndex, false);

        //
        // Don't re-set types, but use something if this
        // parameter was never specified
        //
        if (binding.bufferType == 0) {
            setType(binding, MysqlDefs.FIELD_TYPE_NULL);
        }

        binding.value = null;
        binding.isNull = true;
        binding.isLongData = false;
    }

    /**
     * @see java.sql.PreparedStatement#getParameterMetaData()
     */
    public ParameterMetaData getParameterMetaData() throws SQLException {
        throw new NotImplemented();
    }

    /**
     * @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)
     */
    public void setRef(int i, Ref x) throws SQLException {
        throw new NotImplemented();
    }

    /**
     * @see java.sql.PreparedStatement#setShort(int, short)
     */
    public void setShort(int parameterIndex, short x) throws SQLException {
        checkClosed();

        BindValue binding = getBinding(parameterIndex, false);
        setType(binding, MysqlDefs.FIELD_TYPE_SHORT);

        binding.value = null;
        binding.shortBinding = x;
        binding.isNull = false;
        binding.isLongData = false;
    }

    /**
     * @see java.sql.PreparedStatement#setString(int, java.lang.String)
     */
    public void setString(int parameterIndex, String x)
        throws SQLException {
        checkClosed();

        if (x == null) {
            setNull(parameterIndex, java.sql.Types.CHAR);
        } else {
            BindValue binding = getBinding(parameterIndex, false);

            setType(binding, this.stringTypeCode);

            binding.value = x;
            binding.isNull = false;
            binding.isLongData = false;
        }
    }

    /**
     * Set a parameter to a java.sql.Time value.  The driver converts this to a
     * SQL TIME value when it sends it to the database, using the given
     * timezone.
     *
     * @param parameterIndex the first parameter is 1...));
     * @param x the parameter value
     * @param cal the timezone to use
     *
     * @throws SQLException if a database access error occurs
     */
    public void setTime(int parameterIndex, java.sql.Time x, Calendar cal)
        throws SQLException {
        setTimeInternal(parameterIndex, x, cal.getTimeZone(), true);
    }

    /**
     * Set a parameter to a java.sql.Time value.
     *
     * @param parameterIndex the first parameter is 1...));
     * @param x the parameter value
     *
     * @throws SQLException if a database access error occurs
     */
    public void setTime(int parameterIndex, java.sql.Time x)
        throws SQLException {
        setTimeInternal(parameterIndex, x, TimeZone.getDefault(), false);
    }

    /**
     * Set a parameter to a java.sql.Time value.  The driver converts this to a
     * SQL TIME value when it sends it to the database, using the given
     * timezone.
     *
     * @param parameterIndex the first parameter is 1...));
     * @param x the parameter value
     * @param tz the timezone to use
     *
     * @throws SQLException if a database access error occurs
     */
    public void setTimeInternal(int parameterIndex, java.sql.Time x, TimeZone tz, boolean rollForward)
        throws SQLException {
        if (x == null) {
            setNull(parameterIndex, java.sql.Types.TIME);
        } else {
            BindValue binding = getBinding(parameterIndex, false);
            setType(binding, MysqlDefs.FIELD_TYPE_TIME);

            binding.value = TimeUtil.changeTimezone(this.connection, x, tz,
                    this.connection.getServerTimezoneTZ(), rollForward);
            binding.isNull = false;
            binding.isLongData = false;
        }
    }

    /**
     * Set a parameter to a java.sql.Timestamp value.  The driver converts this
     * to a SQL TIMESTAMP value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     *
     * @throws SQLException if a database-access error occurs.
     */
    public void setTimestamp(int parameterIndex, java.sql.Timestamp x)
        throws SQLException {
        setTimestampInternal(parameterIndex, x, TimeZone.getDefault(), false);
    }

    /**
     * Set a parameter to a java.sql.Timestamp value.  The driver converts this
     * to a SQL TIMESTAMP value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @param cal the timezone to use
     *
     * @throws SQLException if a database-access error occurs.
     */
    public void setTimestamp(int parameterIndex, java.sql.Timestamp x,
        Calendar cal) throws SQLException {
        setTimestampInternal(parameterIndex, x, cal.getTimeZone(), true);
    }

    /**
     * @see java.sql.PreparedStatement#setURL(int, java.net.URL)
     */
    public void setURL(int parameterIndex, URL x) throws SQLException {
        checkClosed();

        setString(parameterIndex, x.toString());
    }

    /**
     * DOCUMENT ME!
     *
     * @param parameterIndex DOCUMENT ME!
     * @param x DOCUMENT ME!
     * @param length DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     * @throws NotImplemented DOCUMENT ME!
     *
     * @see java.sql.PreparedStatement#setUnicodeStream(int,
     *      java.io.InputStream, int)
     * @deprecated
     */
    public void setUnicodeStream(int parameterIndex, InputStream x, int length)
        throws SQLException {
        checkClosed();

        throw new NotImplemented();
    }

    /**
     * JDBC 2.0 Add a set of parameters to the batch.
     *
     * @exception SQLException if a database-access error occurs.
     *
     * @see Statement#addBatch
     */
    public synchronized void addBatch() throws SQLException {
        checkClosed();

        if (this.batchedArgs == null) {
            this.batchedArgs = new ArrayList();
        }

        this.batchedArgs.add(new BatchedBindValues(this.parameterBindings));
    }

    /**
     * @see java.sql.PreparedStatement#clearParameters()
     */
    public void clearParameters() throws SQLException {
        clearParametersInternal(true);
    }

    /**
     * @see java.sql.Statement#close()
     */
    public void close() throws SQLException {
        realClose(true);
    }

    /**
     * @see java.sql.Statement#executeBatch()
     */
    public synchronized int[] executeBatch() throws SQLException {
        if (this.connection.isReadOnly()) {
            throw new SQLException(Messages.getString(
                    "ServerPreparedStatement.2") //$NON-NLS-1$
                 +Messages.getString("ServerPreparedStatement.3"), //$NON-NLS-1$
                SQLError.SQL_STATE_ILLEGAL_ARGUMENT);
        }

        checkClosed();

        synchronized (this.connection.getMutex()) {
            clearWarnings();

            // Store this for later, we're going to 'swap' them out
            // as we execute each batched statement...
            BindValue[] oldBindValues = this.parameterBindings;

            try {
                int[] updateCounts = null;

                if (this.batchedArgs != null) {
                    int nbrCommands = this.batchedArgs.size();
                    updateCounts = new int[nbrCommands];

                    if (this.retrieveGeneratedKeys) {
                        this.batchedGeneratedKeys = new ArrayList(nbrCommands);
                    }

                    for (int i = 0; i < nbrCommands; i++) {
                        updateCounts[i] = -3;
                    }

                    SQLException sqlEx = null;

                    int commandIndex = 0;

                    BindValue[] previousBindValuesForBatch = null;
                    
                    for (commandIndex = 0; commandIndex < nbrCommands;
                            commandIndex++) {
                        Object arg = this.batchedArgs.get(commandIndex);

                        if (arg instanceof String) {
                            updateCounts[commandIndex] = executeUpdate((String) arg);
                        } else {
                            this.parameterBindings = ((BatchedBindValues) arg).batchedParameterValues;

                            try {
                            	// We need to check types each time, as
                            	// the user might have bound different
                            	// types in each addBatch()

                            	if (previousBindValuesForBatch != null) {
                            		for (int j = 0; j < this.parameterBindings.length; j++) {
                            			if (this.parameterBindings[j].bufferType != previousBindValuesForBatch[j].bufferType) {
                            				this.sendTypesToServer = true;
                            			
                            				break;
                            			}
                            		}
                            	}

                            	try {
                            		updateCounts[commandIndex] = executeUpdate(false);
                            	} finally {
                            		previousBindValuesForBatch = this.parameterBindings;
                            	}

                                if (this.retrieveGeneratedKeys) {
                                    java.sql.ResultSet rs = null;

                                    try {
                                        // we don't want to use our version,
                                        // because we've altered the behavior of ours to support batch updates
                                        // (catch-22)
                                        // Ideally, what we need here is super.super.getGeneratedKeys()
                                        // but that construct doesn't exist in Java, so that's why there's
                                        // this kludge.
                                        rs = getGeneratedKeysInternal();

                                        while (rs.next()) {
                                            this.batchedGeneratedKeys.add(new byte[][] {
                                                    rs.getBytes(1)
                                                });
                                        }
                                    } finally {
                                        if (rs != null) {
                                            rs.close();
                                        }
                                    }
                                }
                            } catch (SQLException ex) {
                                updateCounts[commandIndex] = EXECUTE_FAILED;

                                if (this.connection.getContinueBatchOnError()) {
                                    sqlEx = ex;
                                } else {
                                    int[] newUpdateCounts = new int[commandIndex];
                                    System.arraycopy(updateCounts, 0,
                                        newUpdateCounts, 0, commandIndex);

                                    throw new java.sql.BatchUpdateException(ex.getMessage(),
                                        ex.getSQLState(), ex.getErrorCode(),
                                        newUpdateCounts);
                                }
                            }
                        }
                    }

                    if (sqlEx != null) {
                        throw new java.sql.BatchUpdateException(sqlEx.getMessage(),
                            sqlEx.getSQLState(), sqlEx.getErrorCode(),
                            updateCounts);
                    }
                }

                return (updateCounts != null) ? updateCounts : new int[0];
            } finally {
                this.parameterBindings = oldBindValues;
                this.sendTypesToServer = true;
                
                clearBatch();
            }
        }
    }
    
    public String asSql() throws SQLException {
    	return asSql(false);
    }
    
    protected String asSql(boolean quoteStreamsAndUnknowns) throws SQLException {
    	
    	PreparedStatement pStmtForSub = null;
    	
    	try {
    		pStmtForSub = new PreparedStatement(this.connection,
    				this.originalSql, this.currentCatalog);
	
	        int numParameters = pStmtForSub.parameterCount;
	        int ourNumParameters = this.parameterCount;
	
	        for (int i = 0; (i < numParameters) && (i < ourNumParameters);
	                i++) {
	            if (this.parameterBindings[i] != null) {
	                if (this.parameterBindings[i].isNull) {
	                    pStmtForSub.setNull(i + 1, Types.NULL);
	                } else {
	                	BindValue bindValue = this.parameterBindings[i];
	                	
	                	//
	                    // Handle primitives first
	                    //
	                    switch (bindValue.bufferType) {
	         
	                    case MysqlDefs.FIELD_TYPE_TINY:
	                        pStmtForSub.setByte(i + 1,bindValue.byteBinding);
	                        break;
	                    case MysqlDefs.FIELD_TYPE_SHORT:
	                    	pStmtForSub.setShort(i + 1,bindValue.shortBinding);
	                        break;
	                    case MysqlDefs.FIELD_TYPE_LONG:
	                    	pStmtForSub.setInt(i + 1,bindValue.intBinding);
	                    	break;
	                    case MysqlDefs.FIELD_TYPE_LONGLONG:
	                    	pStmtForSub.setLong(i + 1,bindValue.longBinding);
	                    	break;
	                    case MysqlDefs.FIELD_TYPE_FLOAT:
	                        pStmtForSub.setFloat(i + 1,bindValue.floatBinding);
	                        break;
	                    case MysqlDefs.FIELD_TYPE_DOUBLE:
	                        pStmtForSub.setDouble(i + 1,bindValue.doubleBinding);
	                        break;
	                    default:
	                    	pStmtForSub.setObject(i + 1,
	                                this.parameterBindings[i].value);
	                    	break;
	                    }
	                }
	            }
	        }
	
	        return pStmtForSub.asSql(quoteStreamsAndUnknowns);	
    	} finally {
    		if (pStmtForSub != null) {
                try {
                    pStmtForSub.close();
                } catch (SQLException sqlEx) {
                    ; // ignore
                }
            }
    	}
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer toStringBuf = new StringBuffer();

        try {
        	toStringBuf.append("com.mysql.jdbc.ServerPreparedStatement["); //$NON-NLS-1$
        	toStringBuf.append(this.serverStatementId);
        	toStringBuf.append("] - "); //$NON-NLS-1$
        	toStringBuf.append(asSql());
        } catch (SQLException sqlEx) {
            toStringBuf.append(Messages.getString("ServerPreparedStatement.6")); //$NON-NLS-1$
            toStringBuf.append(sqlEx);
        }

        return toStringBuf.toString();
    }

    protected void setTimestampInternal(int parameterIndex,
        java.sql.Timestamp x, TimeZone tz, boolean rollForward) throws SQLException {
        if (x == null) {
            setNull(parameterIndex, java.sql.Types.TIMESTAMP);
        } else {
            BindValue binding = getBinding(parameterIndex, false);
            setType(binding, MysqlDefs.FIELD_TYPE_DATETIME);

            binding.value = TimeUtil.changeTimezone(this.connection, x, tz,
                    this.connection.getServerTimezoneTZ(), rollForward);
            binding.isNull = false;
            binding.isLongData = false;
        }
    }

    /* (non-Javadoc)
     * @see com.mysql.jdbc.Statement#checkClosed()
     */
    protected void checkClosed() throws SQLException {
        if (this.invalid) {
            throw this.invalidationException;
        }

        super.checkClosed();
    }

    /**
     * @see com.mysql.jdbc.PreparedStatement#executeInternal(int,
     *      com.mysql.jdbc.Buffer, boolean, boolean)
     */
    protected com.mysql.jdbc.ResultSet executeInternal(int maxRowsToRetrieve,
        Buffer sendPacket, boolean createStreamingResultSet,
        boolean queryIsSelectOnly, boolean unpackFields)
        throws SQLException {
        this.numberOfExecutions++;

        // We defer to server-side execution

        return serverExecute(maxRowsToRetrieve, createStreamingResultSet);
    }

    /**
     * @see com.mysql.jdbc.PreparedStatement#fillSendPacket()
     */
    protected Buffer fillSendPacket() throws SQLException {
        return null; // we don't use this type of packet
    }

    /**
     * @see com.mysql.jdbc.PreparedStatement#fillSendPacket(byte,
     *      java.io.InputStream, boolean, int)
     */
    protected Buffer fillSendPacket(byte[][] batchedParameterStrings,
        InputStream[] batchedParameterStreams, boolean[] batchedIsStream,
        int[] batchedStreamLengths) throws SQLException {
        return null; // we don't use this type of packet
    }

    /**
     * Used by Connection when auto-reconnecting to retrieve 'lost' prepared
     * statements.
     *
     * @throws SQLException if an error occurs.
     */
    protected void rePrepare() throws SQLException {
        this.invalidationException = null;
        
        // FIXME: How to do this now????
        
        /*
        try {
            //serverPrepare(this.originalSql);
        } catch (SQLException sqlEx) {
            // don't wrap SQLExceptions
            this.invalidationException = sqlEx;
        } catch (Exception ex) {
            this.invalidationException = new SQLException(ex.toString(),
                    SQLError.SQL_STATE_GENERAL_ERROR);
        }

        if (this.invalidationException != null) {
            this.invalid = true;

            this.parameterBindings = null;

            this.parameterFields = null;
            this.resultFields = null;

            if (this.results != null) {
                try {
                    this.results.close();
                } catch (Exception ex) {
                    ;
                }
            }

            if (this.connection != null) {
                if (this.maxRowsChanged) {
                    this.connection.unsetMaxRows(this);
                }

                this.connection.unregisterStatement(this);
            }
        }*/
    }

    /**
     * Closes this connection and frees all resources.
     *
     * @param calledExplicitly was this called from close()?
     *
     * @throws SQLException if an error occurs
     */
    protected void realClose(boolean calledExplicitly)
        throws SQLException {
        if (this.isClosed) {
            return;
        }

        SQLException exceptionDuringClose = null;

        try {
        	this.connection.closeServerPreparedStatement(this.serverStatementId);
        } catch (SQLException sqlEx) {
            exceptionDuringClose = sqlEx;
        }

        clearParametersInternal(false);
        this.parameterBindings = null;

        this.parameterFields = null;
        this.resultFields = null;

        super.realClose(calledExplicitly);

        if (exceptionDuringClose != null) {
            throw exceptionDuringClose;
        }
    }

    /**
     * @see com.mysql.jdbc.PreparedStatement#getBytes(int)
     */
    synchronized byte[] getBytes(int parameterIndex) throws SQLException {
    	// FIXME: Does this actually need to be implemented?
    	
        BindValue bindValue = getBinding(parameterIndex, false);

        if (bindValue.isNull) {
            return null;
        } else if (bindValue.isLongData) {
            throw new NotImplemented();
        } else {
            if (this.outByteBuffer == null) {
                this.outByteBuffer = Buffer.allocateNew(this.connection.getNetBufferLength(),
                        false);
            }

            this.outByteBuffer.clear();

            int originalPosition = this.outByteBuffer.getPosition();

            this.connection.storeBinding(this, this.outByteBuffer, bindValue);

            int newPosition = this.outByteBuffer.getPosition();

            int length = newPosition - originalPosition;

            byte[] valueAsBytes = new byte[length];

            System.arraycopy(this.outByteBuffer.getByteBuffer(),
                originalPosition, valueAsBytes, 0, length);

            return valueAsBytes;
        }
    }

    /**
     * @see com.mysql.jdbc.PreparedStatement#isNull(int)
     */
    boolean isNull(int paramIndex) {
        throw new IllegalArgumentException(Messages.getString(
                "ServerPreparedStatement.7")); //$NON-NLS-1$
    }

    private BindValue getBinding(int parameterIndex, boolean forLongData)
        throws SQLException {
        if (this.parameterBindings.length == 0) {
            throw new SQLException(Messages.getString(
                    "ServerPreparedStatement.8"), //$NON-NLS-1$
                SQLError.SQL_STATE_ILLEGAL_ARGUMENT);
        }

        parameterIndex--;

        if ((parameterIndex < 0) ||
                (parameterIndex >= this.parameterBindings.length)) {
            throw new SQLException(Messages.getString(
                    "ServerPreparedStatement.9") //$NON-NLS-1$
                 +(parameterIndex + 1) +
                Messages.getString("ServerPreparedStatement.10") //$NON-NLS-1$
                 +this.parameterBindings.length,
                SQLError.SQL_STATE_ILLEGAL_ARGUMENT);
        }

        if (this.parameterBindings[parameterIndex] == null) {
            this.parameterBindings[parameterIndex] = new BindValue();
        } else {
            if (this.parameterBindings[parameterIndex].isLongData &&
                    !forLongData) {
                this.detectedLongParameterSwitch = true;
            }  
        }

        this.parameterBindings[parameterIndex].isSet = true;
        
        return this.parameterBindings[parameterIndex];
    }

    private void setType(BindValue oldValue, int bufferType) {
        if (oldValue.bufferType != bufferType) {
            this.sendTypesToServer = true;
        }

        oldValue.bufferType = bufferType;
    }

    private void clearParametersInternal(boolean clearServerParameters)
        throws SQLException {
        boolean hadLongData = false;

        if (this.parameterBindings != null) {
            for (int i = 0; i < this.parameterCount; i++) {
                if ((this.parameterBindings[i] != null) &&
                        this.parameterBindings[i].isLongData) {
                    hadLongData = true;
                }

                this.parameterBindings[i].reset();
            }
        }

        if (clearServerParameters && hadLongData) {
            this.connection.serverResetStatement(this);
            this.detectedLongParameterSwitch = false;
        }
    }

    /**
	 * @return Returns the serverStatementId.
	 */
	protected long getServerStatementId() {
		return serverStatementId;
	}

	/**
	 * @return
	 */
	public boolean isLoadDataQuery() {
		return this.isLoadDataQuery;
	}

	/**
	 * @return
	 */
	protected SingleByteCharsetConverter getCharConverter() {
		return this.charConverter;
	}

	/**
	 * @return
	 */
	public String getCharEncoding() {
		return this.charEncoding;
	}
	
	private com.mysql.jdbc.ResultSet serverExecute(int maxRowsToRetrieve,
			boolean createStreamingResultSet) throws SQLException {
		
		if (this.detectedLongParameterSwitch) {
			throw new SQLException(Messages.getString(
			"ServerPreparedStatement.11") //$NON-NLS-1$
			+Messages.getString("ServerPreparedStatement.12"), //$NON-NLS-1$
			SQLError.SQL_STATE_DRIVER_NOT_CAPABLE);
		}
		
		// Check bindings
		for (int i = 0; i < this.parameterCount; i++) {
			if (!this.parameterBindings[i].isSet) {
				throw new SQLException(Messages.getString(
				"ServerPreparedStatement.13") + (i + 1) //$NON-NLS-1$
				+Messages.getString("ServerPreparedStatement.14"),
				SQLError.SQL_STATE_ILLEGAL_ARGUMENT); //$NON-NLS-1$
			}
		}
		
		com.mysql.jdbc.ResultSet rs = this.connection.serverExecute(this,
				this.parameterBindings, 
				this.fieldCount,
				maxRowsToRetrieve,
				this.sendTypesToServer,
				createStreamingResultSet);
		
		this.results = rs;
		
		this.sendTypesToServer = false;
		
		return rs;
	}
        

	/**
	 * @return
	 */
	public String getOriginalSql() {
		return this.originalSql;
	}

    /**
     * Method storeBinding.
     *
     * @param packet
     * @param bindValue
     * @param mysql DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private void storeBinding(Buffer packet, BindValue bindValue, MysqlIO mysql)
        throws SQLException {
        try {
            Object value = bindValue.value;

            //
            // Handle primitives first
            //
            switch (bindValue.bufferType) {
 
            case MysqlDefs.FIELD_TYPE_TINY:
                packet.writeByte(bindValue.byteBinding);
                return;
            case MysqlDefs.FIELD_TYPE_SHORT:
                packet.ensureCapacity(2);
                packet.writeInt(bindValue.shortBinding);
                return;
            case MysqlDefs.FIELD_TYPE_LONG:
                packet.ensureCapacity(4);
                packet.writeLong(bindValue.intBinding);
                return;
            case MysqlDefs.FIELD_TYPE_LONGLONG:
                packet.ensureCapacity(8);
                packet.writeLongLong(bindValue.longBinding);
                return;
            case MysqlDefs.FIELD_TYPE_FLOAT:
                packet.ensureCapacity(4);
                packet.writeFloat(bindValue.floatBinding);
                return;
            case MysqlDefs.FIELD_TYPE_DOUBLE:
                packet.ensureCapacity(8);
                packet.writeDouble(bindValue.doubleBinding);
                return;
            case MysqlDefs.FIELD_TYPE_TIME: 
                storeTime(packet, (Time) value);
            	return;
            case MysqlDefs.FIELD_TYPE_DATE:
            case MysqlDefs.FIELD_TYPE_DATETIME:
            case MysqlDefs.FIELD_TYPE_TIMESTAMP:
                storeDateTime(packet, (java.util.Date) value, mysql);
            	return;
            case MysqlDefs.FIELD_TYPE_VAR_STRING:
            case MysqlDefs.FIELD_TYPE_STRING:
            	if (!this.isLoadDataQuery) {
                    packet.writeLenString((String) value, this.charEncoding,
                        this.connection.getServerCharacterEncoding(),
                        this.charConverter, this.connection.parserKnowsUnicode());
                } else {
                    packet.writeLenBytes(((String) value).getBytes());
                }
            	
            	return;
            }
            
            if (value instanceof byte[]) {
                packet.writeLenBytes((byte[]) value);
            }
        } catch (UnsupportedEncodingException uEE) {
            throw new SQLException(Messages.getString(
                    "ServerPreparedStatement.22") //$NON-NLS-1$
                 +this.connection.getEncoding() + "'", //$NON-NLS-1$
                SQLError.SQL_STATE_GENERAL_ERROR);
        }
    }

    private void storeDataTime412AndOlder(Buffer intoBuf, java.util.Date dt)
        throws SQLException {
        // This is synchronized on the connection by callers, so it is
        // safe to lazily-instantiate this...
        if (this.dateTimeBindingCal == null) {
        	this.dateTimeBindingCal = Calendar.getInstance();
        }

        this.dateTimeBindingCal.setTime(dt);

        intoBuf.ensureCapacity(8);
        intoBuf.writeByte((byte) 7); // length

        int year = this.dateTimeBindingCal.get(Calendar.YEAR);
        int month = this.dateTimeBindingCal.get(Calendar.MONTH) + 1;
        int date = this.dateTimeBindingCal.get(Calendar.DATE);

        intoBuf.writeInt(year);
        intoBuf.writeByte((byte) month);
        intoBuf.writeByte((byte) date);

        if (dt instanceof java.sql.Date) {
            intoBuf.writeByte((byte) 0);
            intoBuf.writeByte((byte) 0);
            intoBuf.writeByte((byte) 0);
        } else {
            intoBuf.writeByte((byte) this.dateTimeBindingCal.get(
                    Calendar.HOUR_OF_DAY));
            intoBuf.writeByte((byte) this.dateTimeBindingCal.get(Calendar.MINUTE));
            intoBuf.writeByte((byte) this.dateTimeBindingCal.get(Calendar.SECOND));
        }
    }

    private void storeDateTime(Buffer intoBuf, java.util.Date dt, MysqlIO mysql)
        throws SQLException {
        if (this.connection.versionMeetsMinimum(4, 1, 3)) {
            storeDateTime413AndNewer(intoBuf, dt);
        } else {
            storeDataTime412AndOlder(intoBuf, dt);
        }
    }

    private void storeDateTime413AndNewer(Buffer intoBuf, java.util.Date dt)
        throws SQLException {
        // This is synchronized on the connection by callers, so it is
        // safe to lazily-instantiate this...
        if (this.dateTimeBindingCal == null) {
        	this.dateTimeBindingCal = Calendar.getInstance();
        }

        this.dateTimeBindingCal.setTime(dt);

        byte length = (byte) 7;

        intoBuf.ensureCapacity(length);

        if (dt instanceof java.sql.Timestamp) {
            length = (byte) 11;
        }

        intoBuf.writeByte(length); // length

        int year = this.dateTimeBindingCal.get(Calendar.YEAR);
        int month = this.dateTimeBindingCal.get(Calendar.MONTH) + 1;
        int date = this.dateTimeBindingCal.get(Calendar.DATE);

        intoBuf.writeInt(year);
        intoBuf.writeByte((byte) month);
        intoBuf.writeByte((byte) date);

        if (dt instanceof java.sql.Date) {
            intoBuf.writeByte((byte) 0);
            intoBuf.writeByte((byte) 0);
            intoBuf.writeByte((byte) 0);
        } else {
            intoBuf.writeByte((byte) this.dateTimeBindingCal.get(
                    Calendar.HOUR_OF_DAY));
            intoBuf.writeByte((byte) this.dateTimeBindingCal.get(Calendar.MINUTE));
            intoBuf.writeByte((byte) this.dateTimeBindingCal.get(Calendar.SECOND));
        }

        if (length == 11) {
            intoBuf.writeLong(((java.sql.Timestamp) dt).getNanos());
        }
    }

    //
    // TO DO: Investigate using NIO to do this faster
    //
    private void storeReader(Buffer packet, Reader inStream)
        throws SQLException {
        char[] buf = new char[4096];
        StringBuffer valueAsString = new StringBuffer();

        int numRead = 0;

        try {
            while ((numRead = inStream.read(buf)) != -1) {
                valueAsString.append(buf, 0, numRead);
            }
        } catch (IOException ioEx) {
            throw new SQLException(Messages.getString(
                    "ServerPreparedStatement.24") //$NON-NLS-1$
                 +ioEx.toString(), SQLError.SQL_STATE_GENERAL_ERROR);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException ioEx) {
                    ; // ignore
                }
            }
        }

        byte[] valueAsBytes = StringUtils.getBytes(valueAsString.toString(),
                this.connection.getEncoding(),
                this.connection.getServerCharacterEncoding(),
                this.connection.parserKnowsUnicode());

        packet.writeBytesNoNull(valueAsBytes);
    }

    private void storeStream(Buffer packet, InputStream inStream)
        throws SQLException {
        byte[] buf = new byte[4096];

        int numRead = 0;

        try {
            while ((numRead = inStream.read(buf)) != -1) {
                packet.writeBytesNoNull(buf, 0, numRead);
            }
        } catch (IOException ioEx) {
            throw new SQLException(Messages.getString(
                    "ServerPreparedStatement.25") //$NON-NLS-1$
                 +ioEx.toString(), SQLError.SQL_STATE_GENERAL_ERROR);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException ioEx) {
                    ; // ignore
                }
            }
        }
    }

    private static void storeTime(Buffer intoBuf, Time tm)
        throws SQLException {
        intoBuf.ensureCapacity(9);
        intoBuf.writeByte((byte) 8); // length
        intoBuf.writeByte((byte) 0); // neg flag
        intoBuf.writeLong(0); // tm->day, not used

        Calendar cal = Calendar.getInstance();
        cal.setTime(tm);
        intoBuf.writeByte((byte) cal.get(Calendar.HOUR_OF_DAY));
        intoBuf.writeByte((byte) cal.get(Calendar.MINUTE));
        intoBuf.writeByte((byte) cal.get(Calendar.SECOND));

        //intoBuf.writeLongInt(0); // tm-second_part
    }

    static class BatchedBindValues {
        BindValue[] batchedParameterValues;

        BatchedBindValues(BindValue[] paramVals) {
            int numParams = paramVals.length;

            this.batchedParameterValues = new BindValue[numParams];

            for (int i = 0; i < numParams; i++) {
            	this.batchedParameterValues[i] = new BindValue(paramVals[i]);
            }
        }
    }

    /**
	 * @return
	 */
	public String getCurrentCatalog() {
		return this.currentCatalog;
	}
}
