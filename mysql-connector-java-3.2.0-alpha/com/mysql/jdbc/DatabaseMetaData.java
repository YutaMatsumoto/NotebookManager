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

import java.io.UnsupportedEncodingException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;


/**
 * JDBC Interface to Mysql functions
 *
 * <p>
 * This class provides information about the database as a whole.
 * </p>
 *
 * <p>
 * Many of the methods here return lists of information in ResultSets. You can
 * use the normal ResultSet methods such as getString and getInt to retrieve
 * the data from these ResultSets.  If a given form of metadata is not
 * available, these methods show throw a SQLException.
 * </p>
 *
 * <p>
 * Some of these methods take arguments that are String patterns.  These
 * methods all have names such as fooPattern.  Within a pattern String "%"
 * means match any substring of 0 or more characters and "_" means match any
 * one character.
 * </p>
 *
 * @author Mark Matthews
 * @version $Id: DatabaseMetaData.java,v 1.27.4.35.2.9 2004/12/13 22:22:04 mmatthew Exp $
 */
public class DatabaseMetaData implements java.sql.DatabaseMetaData {
	SchemaData schemaData;
	
	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getColumnPrivileges(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getColumnPrivileges(String catalog, String schema,
			String table, String columnNamePattern) throws SQLException {
		return this.schemaData.getColumnPrivileges(catalog, schema, table, columnNamePattern)
;	}
	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getColumns(String catalog, String schemaPattern,
			String tableNamePattern, String columnNamePattern)
			throws SQLException {
		return this.schemaData.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
	}
	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getCrossReference(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getCrossReference(String primaryCatalog,
			String primarySchema, String primaryTable, String foreignCatalog,
			String foreignSchema, String foreignTable) throws SQLException {
		return this.schemaData.getCrossReference(primaryCatalog, primarySchema, primaryTable, foreignCatalog, foreignSchema, foreignTable);
	}
	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getExportedKeys(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getExportedKeys(String catalog, String schema, String table)
			throws SQLException {
		return this.schemaData.getExportedKeys(catalog, schema, table);
	}
	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getImportedKeys(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getImportedKeys(String catalog, String schema, String table)
			throws SQLException {
		return this.schemaData.getImportedKeys(catalog, schema, table);
	}
	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getIndexInfo(java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
	 */
	public ResultSet getIndexInfo(String catalog, String schema, String table,
			boolean unique, boolean approximate) throws SQLException {
		return this.schemaData.getIndexInfo(catalog, schema, table, unique, approximate);
	}
	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getPrimaryKeys(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getPrimaryKeys(String catalog, String schema, String table)
			throws SQLException {
		return this.schemaData.getPrimaryKeys(catalog, schema, table);
	}
	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getProcedureColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getProcedureColumns(String catalog, String schemaPattern,
			String procedureNamePattern, String columnNamePattern)
			throws SQLException {
		return this.schemaData.getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern);
	}
	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getProcedures(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getProcedures(String catalog, String schemaPattern,
			String procedureNamePattern) throws SQLException {
		return this.schemaData.getProcedures(catalog, schemaPattern, procedureNamePattern);
	}
	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSchemas()
	 */
	public ResultSet getSchemas() throws SQLException {
		return this.schemaData.getSchemas();
	}
	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getTablePrivileges(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getTablePrivileges(String catalog, String schemaPattern,
			String tableNamePattern) throws SQLException {
		return this.schemaData.getTablePrivileges(catalog, schemaPattern, tableNamePattern);
	}
	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getTables(java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	public ResultSet getTables(String catalog, String schemaPattern,
			String tableNamePattern, String[] types) throws SQLException {
		return this.schemaData.getTables(catalog, schemaPattern, tableNamePattern, types);
	}
	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getVersionColumns(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getVersionColumns(String catalog, String schema,
			String table) throws SQLException {
		return this.schemaData.getVersionColumns(catalog, schema, table);
	}
	
    private static final byte[] TABLE_AS_BYTES = "TABLE".getBytes();
    private static final byte[] VIEW_AS_BYTES = "VIEW".getBytes();

    /** The table type for generic tables that support foreign keys. */
    private static final String SUPPORTS_FK = "SUPPORTS_FK";

    //
    // Column indexes used by all DBMD foreign key
    // ResultSets
    //
    private static final int PKTABLE_CAT = 0;
    private static final int PKTABLE_SCHEM = 1;
    private static final int PKTABLE_NAME = 2;
    private static final int PKCOLUMN_NAME = 3;
    private static final int FKTABLE_CAT = 4;
    private static final int FKTABLE_SCHEM = 5;
    private static final int FKTABLE_NAME = 6;
    private static final int FKCOLUMN_NAME = 7;
    private static final int KEY_SEQ = 8;
    private static final int UPDATE_RULE = 9;
    private static final int DELETE_RULE = 10;
    private static final int FK_NAME = 11;
    private static final int PK_NAME = 12;
    private static final int DEFERRABILITY = 13;

    /** The connection to the database */
    protected Connection conn;

    /** The 'current' database name being used */
    private String database = null;

    /** What character to use when quoting identifiers */
    private String quotedId = null;

    /**
     * Creates a new DatabaseMetaData object.
     *
     * @param connToSet DOCUMENT ME!
     * @param databaseToSet DOCUMENT ME!
     */
    public DatabaseMetaData(Connection connToSet, String databaseToSet) throws SQLException {
        this.conn = connToSet;
        this.database = databaseToSet;

        try {
            this.quotedId = this.conn.supportsQuotedIdentifiers()
                ? getIdentifierQuoteString() : "";
        } catch (SQLException sqlEx) {
            // Forced by API, never thrown from getIdentifierQuoteString() in this
            // implementation.
            AssertionFailedException.shouldNotHappen(sqlEx);
        }
        
        this.schemaData = new SchemaDataUsingShow(this.conn, this.database, getIdentifierQuoteString());
    }

    /**
     * @see DatabaseMetaData#getAttributes(String, String, String, String)
     */
    public java.sql.ResultSet getAttributes(String arg0, String arg1,
        String arg2, String arg3) throws SQLException {
        Field[] fields = new Field[21];
        fields[0] = new Field("", "TYPE_CAT", Types.CHAR, 32);
        fields[1] = new Field("", "TYPE_SCHEM", Types.CHAR, 32);
        fields[2] = new Field("", "TYPE_NAME", Types.CHAR, 32);
        fields[3] = new Field("", "ATTR_NAME", Types.CHAR, 32);
        fields[4] = new Field("", "DATA_TYPE", Types.SMALLINT, 32);
        fields[5] = new Field("", "ATTR_TYPE_NAME", Types.CHAR, 32);
        fields[6] = new Field("", "ATTR_SIZE", Types.INTEGER, 32);
        fields[7] = new Field("", "DECIMAL_DIGITS", Types.INTEGER, 32);
        fields[8] = new Field("", "NUM_PREC_RADIX", Types.INTEGER, 32);
        fields[9] = new Field("", "NULLABLE ", Types.INTEGER, 32);
        fields[10] = new Field("", "REMARKS", Types.CHAR, 32);
        fields[11] = new Field("", "ATTR_DEF", Types.CHAR, 32);
        fields[12] = new Field("", "SQL_DATA_TYPE", Types.INTEGER, 32);
        fields[13] = new Field("", "SQL_DATETIME_SUB", Types.INTEGER, 32);
        fields[14] = new Field("", "CHAR_OCTET_LENGTH", Types.INTEGER, 32);
        fields[15] = new Field("", "ORDINAL_POSITION", Types.INTEGER, 32);
        fields[16] = new Field("", "IS_NULLABLE", Types.CHAR, 32);
        fields[17] = new Field("", "SCOPE_CATALOG", Types.CHAR, 32);
        fields[18] = new Field("", "SCOPE_SCHEMA", Types.CHAR, 32);
        fields[19] = new Field("", "SCOPE_TABLE", Types.CHAR, 32);
        fields[20] = new Field("", "SOURCE_DATA_TYPE", Types.SMALLINT, 32);

        return buildResultSet(fields, new ArrayList());
    }

    /**
     * Get a description of a table's optimal set of columns that uniquely
     * identifies a row. They are ordered by SCOPE.
     *
     * <P>
     * Each column description has the following columns:
     *
     * <OL>
     * <li>
     * <B>SCOPE</B> short => actual scope of result
     *
     * <UL>
     * <li>
     * bestRowTemporary - very temporary, while using row
     * </li>
     * <li>
     * bestRowTransaction - valid for remainder of current transaction
     * </li>
     * <li>
     * bestRowSession - valid for remainder of current session
     * </li>
     * </ul>
     *
     * </li>
     * <li>
     * <B>COLUMN_NAME</B> String => column name
     * </li>
     * <li>
     * <B>DATA_TYPE</B> short => SQL data type from java.sql.Types
     * </li>
     * <li>
     * <B>TYPE_NAME</B> String => Data source dependent type name
     * </li>
     * <li>
     * <B>COLUMN_SIZE</B> int => precision
     * </li>
     * <li>
     * <B>BUFFER_LENGTH</B> int => not used
     * </li>
     * <li>
     * <B>DECIMAL_DIGITS</B> short  => scale
     * </li>
     * <li>
     * <B>PSEUDO_COLUMN</B> short => is this a pseudo column like an Oracle
     * ROWID
     *
     * <UL>
     * <li>
     * bestRowUnknown - may or may not be pseudo column
     * </li>
     * <li>
     * bestRowNotPseudo - is NOT a pseudo column
     * </li>
     * <li>
     * bestRowPseudo - is a pseudo column
     * </li>
     * </ul>
     *
     * </li>
     * </ol>
     * </p>
     *
     * @param catalog a catalog name; "" retrieves those without a catalog
     * @param schema a schema name; "" retrieves those without a schema
     * @param table a table name
     * @param scope the scope of interest; use same values as SCOPE
     * @param nullable include columns that are nullable?
     *
     * @return ResultSet each row is a column description
     *
     * @throws SQLException DOCUMENT ME!
     */
    public java.sql.ResultSet getBestRowIdentifier(String catalog,
        String schema, String table, int scope, boolean nullable)
        throws SQLException {
        Field[] fields = new Field[8];
        fields[0] = new Field("", "SCOPE", Types.SMALLINT, 5);
        fields[1] = new Field("", "COLUMN_NAME", Types.CHAR, 32);
        fields[2] = new Field("", "DATA_TYPE", Types.SMALLINT, 32);
        fields[3] = new Field("", "TYPE_NAME", Types.CHAR, 32);
        fields[4] = new Field("", "COLUMN_SIZE", Types.INTEGER, 10);
        fields[5] = new Field("", "BUFFER_LENGTH", Types.INTEGER, 10);
        fields[6] = new Field("", "DECIMAL_DIGITS", Types.INTEGER, 10);
        fields[7] = new Field("", "PSEUDO_COLUMN", Types.SMALLINT, 5);

        String databasePart = "";

        if (catalog != null) {
            if (!catalog.equals("")) {
                databasePart = " FROM " + this.quotedId + catalog +
                    this.quotedId;
            }
        } else {
            databasePart = " FROM " + this.quotedId + this.database +
                this.quotedId;
        }

        if (table == null) {
            throw new SQLException("Table not specified.",
                SQLError.SQL_STATE_ILLEGAL_ARGUMENT);
        }

        ResultSet results = null;
        Statement stmt = null;

        try {
            stmt = this.conn.createStatement();
            stmt.setEscapeProcessing(false);

            StringBuffer queryBuf = new StringBuffer("SHOW COLUMNS FROM ");
            queryBuf.append(this.quotedId);
            queryBuf.append(table);
            queryBuf.append(this.quotedId);
            queryBuf.append(databasePart);

            results = stmt.executeQuery(queryBuf.toString());

            ArrayList tuples = new ArrayList();

            while (results.next()) {
                String keyType = results.getString("Key");

                if (keyType != null) {
                    if (StringUtils.startsWithIgnoreCase(keyType, "PRI")) {
                        byte[][] rowVal = new byte[8][];
                        rowVal[0] = Integer.toString(java.sql.DatabaseMetaData.bestRowSession)
                                           .getBytes();
                        rowVal[1] = results.getBytes("Field");

                        String type = results.getString("Type");
                        int size = MysqlIO.getMaxBuf();
                        int decimals = 0;

                        /*
                         * Parse the Type column from MySQL
                         */
                        if (type.indexOf("enum") != -1) {
                            String temp = type.substring(type.indexOf("("),
                                    type.indexOf(")"));
                            java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(temp,
                                    ",");
                            int maxLength = 0;

                            while (tokenizer.hasMoreTokens()) {
                                maxLength = Math.max(maxLength,
                                        (tokenizer.nextToken().length() - 2));
                            }

                            size = maxLength;
                            decimals = 0;
                            type = "enum";
                        } else if (type.indexOf("(") != -1) {
                            if (type.indexOf(",") != -1) {
                                size = Integer.parseInt(type.substring(type.indexOf(
                                                "(") + 1, type.indexOf(",")));
                                decimals = Integer.parseInt(type.substring(type.indexOf(
                                                ",") + 1, type.indexOf(")")));
                            } else {
                                size = Integer.parseInt(type.substring(type.indexOf(
                                                "(") + 1, type.indexOf(")")));
                            }

                            type = type.substring(type.indexOf("("));
                        }

                        rowVal[2] = new byte[0]; // FIXME!
                        rowVal[3] = s2b(type);
                        rowVal[4] = Integer.toString(size + decimals).getBytes();
                        rowVal[5] = Integer.toString(size + decimals).getBytes();
                        rowVal[6] = Integer.toString(decimals).getBytes();
                        rowVal[7] = Integer.toString(java.sql.DatabaseMetaData.bestRowNotPseudo)
                                           .getBytes();
                        tuples.add(rowVal);
                    }
                }
            }

            return buildResultSet(fields, tuples);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (Exception ex) {
                    ;
                }

                results = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex) {
                    ;
                }

                stmt = null;
            }
        }
    }

    /**
     * Does a catalog appear at the start of a qualified table name? (Otherwise
     * it appears at the end)
     *
     * @return true if it appears at the start
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean isCatalogAtStart() throws SQLException {
        return true;
    }

    /**
     * What's the separator between catalog and table name?
     *
     * @return the separator string
     *
     * @throws SQLException DOCUMENT ME!
     */
    public String getCatalogSeparator() throws SQLException {
        return ".";
    }

    /**
     * What's the database vendor's preferred term for "catalog"?
     *
     * @return the vendor term
     *
     * @throws SQLException DOCUMENT ME!
     */
    public String getCatalogTerm() throws SQLException {
        return "database";
    }

    /**
     * Get the catalog names available in this database.  The results are
     * ordered by catalog name.
     *
     * <P>
     * The catalog column is:
     *
     * <OL>
     * <li>
     * <B>TABLE_CAT</B> String => catalog name
     * </li>
     * </ol>
     * </p>
     *
     * @return ResultSet each row has a single String column that is a catalog
     *         name
     *
     * @throws SQLException DOCUMENT ME!
     */
    public java.sql.ResultSet getCatalogs() throws SQLException {
        java.sql.ResultSet results = null;
        java.sql.Statement stmt = null;

        try {
            stmt = this.conn.createStatement();
            stmt.setEscapeProcessing(false);
            results = stmt.executeQuery("SHOW DATABASES");

            java.sql.ResultSetMetaData resultsMD = results.getMetaData();
            Field[] fields = new Field[1];
            fields[0] = new Field("", "TABLE_CAT", Types.VARCHAR,
                    resultsMD.getColumnDisplaySize(1));

            ArrayList tuples = new ArrayList();

            while (results.next()) {
                byte[][] rowVal = new byte[1][];
                rowVal[0] = results.getBytes(1);
                tuples.add(rowVal);
            }

            return buildResultSet(fields, tuples);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException sqlEx) {
                    AssertionFailedException.shouldNotHappen(sqlEx);
                }

                results = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                    AssertionFailedException.shouldNotHappen(sqlEx);
                }

                stmt = null;
            }
        }
    }

    /**
     * JDBC 2.0 Return the connection that produced this metadata object.
     *
     * @return the connection that produced this metadata object.
     *
     * @throws SQLException if a database error occurs
     */
    public java.sql.Connection getConnection() throws SQLException {
        return this.conn;
    }

    /**
     * @see DatabaseMetaData#getDatabaseMajorVersion()
     */
    public int getDatabaseMajorVersion() throws SQLException {
        return this.conn.getServerMajorVersion();
    }

    /**
     * @see DatabaseMetaData#getDatabaseMinorVersion()
     */
    public int getDatabaseMinorVersion() throws SQLException {
        return this.conn.getServerMinorVersion();
    }

    /**
     * What's the name of this database product?
     *
     * @return database product name
     *
     * @throws SQLException DOCUMENT ME!
     */
    public String getDatabaseProductName() throws SQLException {
        return "MySQL";
    }

    /**
     * What's the version of this database product?
     *
     * @return database version
     *
     * @throws SQLException DOCUMENT ME!
     */
    public String getDatabaseProductVersion() throws SQLException {
        return this.conn.getServerVersion();
    }

    //----------------------------------------------------------------------

    /**
     * What's the database's default transaction isolation level?  The values
     * are defined in java.sql.Connection.
     *
     * @return the default isolation level
     *
     * @throws SQLException if a database access error occurs
     *
     * @see Connection
     */
    public int getDefaultTransactionIsolation() throws SQLException {
        if (this.conn.supportsIsolationLevel()) {
            return java.sql.Connection.TRANSACTION_READ_COMMITTED;
        }

        return java.sql.Connection.TRANSACTION_NONE;
    }

    /**
     * What's this JDBC driver's major version number?
     *
     * @return JDBC driver major version
     */
    public int getDriverMajorVersion() {
        return NonRegisteringDriver.getMajorVersionInternal();
    }

    /**
     * What's this JDBC driver's minor version number?
     *
     * @return JDBC driver minor version number
     */
    public int getDriverMinorVersion() {
        return NonRegisteringDriver.getMinorVersionInternal();
    }

    /**
     * What's the name of this JDBC driver?
     *
     * @return JDBC driver name
     *
     * @throws SQLException DOCUMENT ME!
     */
    public String getDriverName() throws SQLException {
        return "MySQL-AB JDBC Driver";
    }

    /**
     * What's the version of this JDBC driver?
     *
     * @return JDBC driver version
     *
     * @throws java.sql.SQLException DOCUMENT ME!
     */
    public String getDriverVersion() throws java.sql.SQLException {
        return "mysql-connector-java-3.2.0-alpha ( $Date: 2004/12/13 22:22:04 $, $Revision: 1.27.4.35.2.9 $ )";
    }

    /**
     * Get all the "extra" characters that can be used in unquoted identifier
     * names (those beyond a-z, 0-9 and _).
     *
     * @return the string containing the extra characters
     *
     * @throws SQLException DOCUMENT ME!
     */
    public String getExtraNameCharacters() throws SQLException {
        return "#@";
    }

    /**
     * What's the string used to quote SQL identifiers? This returns a space "
     * " if identifier quoting isn't supported. A JDBC compliant driver always
     * uses a double quote character.
     *
     * @return the quoting string
     *
     * @throws SQLException DOCUMENT ME!
     */
    public String getIdentifierQuoteString() throws SQLException {
        if (this.conn.supportsQuotedIdentifiers()) {
            if (!this.conn.useAnsiQuotedIdentifiers()) {
                return "`";
            }

            return "\"";
        }

        return " ";
    }

    /**
     * @see DatabaseMetaData#getJDBCMajorVersion()
     */
    public int getJDBCMajorVersion() throws SQLException {
        return 3;
    }

    /**
     * @see DatabaseMetaData#getJDBCMinorVersion()
     */
    public int getJDBCMinorVersion() throws SQLException {
        return 0;
    }

    //----------------------------------------------------------------------
    // The following group of methods exposes various limitations
    // based on the target database with the current driver.
    // Unless otherwise specified, a result of zero means there is no
    // limit, or the limit is not known.

    /**
     * How many hex characters can you have in an inline binary literal?
     *
     * @return max literal length
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxBinaryLiteralLength() throws SQLException {
        return 16777208;
    }

    /**
     * What's the maximum length of a catalog name?
     *
     * @return max name length in bytes
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxCatalogNameLength() throws SQLException {
        return 32;
    }

    /**
     * What's the max length for a character literal?
     *
     * @return max literal length
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxCharLiteralLength() throws SQLException {
        return 16777208;
    }

    /**
     * What's the limit on column name length?
     *
     * @return max literal length
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxColumnNameLength() throws SQLException {
        return 64;
    }

    /**
     * What's the maximum number of columns in a "GROUP BY" clause?
     *
     * @return max number of columns
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxColumnsInGroupBy() throws SQLException {
        return 64;
    }

    /**
     * What's the maximum number of columns allowed in an index?
     *
     * @return max columns
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxColumnsInIndex() throws SQLException {
        return 16;
    }

    /**
     * What's the maximum number of columns in an "ORDER BY" clause?
     *
     * @return max columns
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxColumnsInOrderBy() throws SQLException {
        return 64;
    }

    /**
     * What's the maximum number of columns in a "SELECT" list?
     *
     * @return max columns
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxColumnsInSelect() throws SQLException {
        return 256;
    }

    /**
     * What's maximum number of columns in a table?
     *
     * @return max columns
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxColumnsInTable() throws SQLException {
        return 512;
    }

    /**
     * How many active connections can we have at a time to this database?
     *
     * @return max connections
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxConnections() throws SQLException {
        return 0;
    }

    /**
     * What's the maximum cursor name length?
     *
     * @return max cursor name length in bytes
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxCursorNameLength() throws SQLException {
        return 64;
    }

    /**
     * What's the maximum length of an index (in bytes)?
     *
     * @return max index length in bytes
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxIndexLength() throws SQLException {
        return 256;
    }

    /**
     * What's the maximum length of a procedure name?
     *
     * @return max name length in bytes
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxProcedureNameLength() throws SQLException {
        return 0;
    }

    /**
     * What's the maximum length of a single row?
     *
     * @return max row size in bytes
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxRowSize() throws SQLException {
        return Integer.MAX_VALUE - 8; // Max buffer size - HEADER
    }

    /**
     * What's the maximum length allowed for a schema name?
     *
     * @return max name length in bytes
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxSchemaNameLength() throws SQLException {
        return 0;
    }

    /**
     * What's the maximum length of a SQL statement?
     *
     * @return max length in bytes
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxStatementLength() throws SQLException {
        return MysqlIO.getMaxBuf() - 4; // Max buffer - header
    }

    /**
     * How many active statements can we have open at one time to this
     * database?
     *
     * @return the maximum
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxStatements() throws SQLException {
        return 0;
    }

    /**
     * What's the maximum length of a table name?
     *
     * @return max name length in bytes
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxTableNameLength() throws SQLException {
        return 64;
    }

    /**
     * What's the maximum number of tables in a SELECT?
     *
     * @return the maximum
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxTablesInSelect() throws SQLException {
        return 256;
    }

    /**
     * What's the maximum length of a user name?
     *
     * @return max name length  in bytes
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int getMaxUserNameLength() throws SQLException {
        return 16;
    }

    /**
     * Get a comma separated list of math functions.
     *
     * @return the list
     *
     * @throws SQLException DOCUMENT ME!
     */
    public String getNumericFunctions() throws SQLException {
        return "ABS,ACOS,ASIN,ATAN,ATAN2,BIT_COUNT,CEILING,COS," +
        "COT,DEGREES,EXP,FLOOR,LOG,LOG10,MAX,MIN,MOD,PI,POW," +
        "POWER,RADIANS,RAND,ROUND,SIN,SQRT,TAN,TRUNCATE";
    }

    /**
     * What's the database vendor's preferred term for "procedure"?
     *
     * @return the vendor term
     *
     * @throws SQLException if an error occurs (don't know why it would in this
     *         case...)
     */
    public String getProcedureTerm() throws SQLException {
        return "PROCEDURE";
    }

    /**
     * Is the database in read-only mode?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    /**
     * @see DatabaseMetaData#getResultSetHoldability()
     */
    public int getResultSetHoldability() throws SQLException {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    /**
     * Get a comma separated list of all a database's SQL keywords that are NOT
     * also SQL92 keywords.
     *
     * @return the list
     *
     * @throws SQLException DOCUMENT ME!
     */
    public String getSQLKeywords() throws SQLException {
        return "AUTO_INCREMENT,BINARY,BLOB,ENUM,INFILE,LOAD,MEDIUMINT,OPTION,OUTFILE,REPLACE,SET,TEXT,UNSIGNED,ZEROFILL";
        
        /* 
           [20:44] root@test> select GROUP_CONCAT(reserved.a) from reserved left 
           join sql92 on (reserved.a=sql92.a) where sql92.a IS NULL GROUP BY 
           (reserved.b) \G
           *************************** 1. row ***************************
           GROUP_CONCAT(reserved.a): RETURN,REQUIRE,REPLACE,REPEAT,RENAME,
           REGEXP,PURGE,SPECIFIC,SPATIAL,SONAME,SHOW,SEPARATOR,SENSITIVE,
           SECOND_MICROSECOND,RLIKE,MOD,MINUTE_SECOND,MINUTE_MICROSECOND,
           MIDDLEINT,MEDIUMTEXT,MEDIUMINT,MEDIUMBLOB,MASTER_SERVER_ID,
           LOW_PRIORITY,LOOP,LONGTEXT,OUTFILE,OUT,OPTIONALLY,OPTIMIZE,
           NO_WRITE_TO_BINLOG,LONGBLOB,ZEROFILL,UTC_DATE,USER_RESOURCES,USE,
           UNSIGNED,UNLOCK,UNDO,UTC_TIME,UTC_TIMESTAMP,YEAR_MONTH,XOR,WHILE,
           VARCHARACTER,VARBINARY,TINYTEXT,SQL_T 
        */
    }

    /**
     * @see DatabaseMetaData#getSQLStateType()
     */
    public int getSQLStateType() throws SQLException {
        if (this.conn.versionMeetsMinimum(4, 1, 0)) {
            return DatabaseMetaData.sqlStateSQL99;
        }

        if (this.conn.getUseSqlStateCodes()) {
            return DatabaseMetaData.sqlStateSQL99;
        }

        return DatabaseMetaData.sqlStateXOpen;
    }

    /**
     * What's the database vendor's preferred term for "schema"?
     *
     * @return the vendor term
     *
     * @throws SQLException DOCUMENT ME!
     */
    public String getSchemaTerm() throws SQLException {
        return "";
    }

    /**
     * This is the string that can be used to escape '_' or '%' in the string
     * pattern style catalog search parameters.
     *
     * <P>
     * The '_' character represents any single character.
     * </p>
     *
     * <P>
     * The '%' character represents any sequence of zero or more characters.
     * </p>
     *
     * @return the string used to escape wildcard characters
     *
     * @throws SQLException DOCUMENT ME!
     */
    public String getSearchStringEscape() throws SQLException {
        return "\\";
    }

    /**
     * Get a comma separated list of string functions.
     *
     * @return the list
     *
     * @throws SQLException DOCUMENT ME!
     */
    public String getStringFunctions() throws SQLException {
        return "ASCII,BIN,BIT_LENGTH,CHAR,CHARACTER_LENGTH,CHAR_LENGTH,CONCAT," +
        "CONCAT_WS,CONV,ELT,EXPORT_SET,FIELD,FIND_IN_SET,HEX,INSERT," +
        "INSTR,LCASE,LEFT,LENGTH,LOAD_FILE,LOCATE,LOCATE,LOWER,LPAD," +
        "LTRIM,MAKE_SET,MATCH,MID,OCT,OCTET_LENGTH,ORD,POSITION," +
        "QUOTE,REPEAT,REPLACE,REVERSE,RIGHT,RPAD,RTRIM,SOUNDEX," +
        "SPACE,STRCMP,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING," +
        "SUBSTRING_INDEX,TRIM,UCASE,UPPER";
    }

    /**
     * @see DatabaseMetaData#getSuperTables(String, String, String)
     */
    public java.sql.ResultSet getSuperTables(String arg0, String arg1,
        String arg2) throws SQLException {
        Field[] fields = new Field[4];
        fields[0] = new Field("", "TABLE_CAT", Types.CHAR, 32);
        fields[1] = new Field("", "TABLE_SCHEM", Types.CHAR, 32);
        fields[2] = new Field("", "TABLE_NAME", Types.CHAR, 32);
        fields[3] = new Field("", "SUPERTABLE_NAME", Types.CHAR, 32);

        return buildResultSet(fields, new ArrayList());
    }

    /**
     * @see DatabaseMetaData#getSuperTypes(String, String, String)
     */
    public java.sql.ResultSet getSuperTypes(String arg0, String arg1,
        String arg2) throws SQLException {
        Field[] fields = new Field[6];
        fields[0] = new Field("", "TABLE_CAT", Types.CHAR, 32);
        fields[1] = new Field("", "TABLE_SCHEM", Types.CHAR, 32);
        fields[2] = new Field("", "TYPE_NAME", Types.CHAR, 32);
        fields[3] = new Field("", "SUPERTYPE_CAT", Types.CHAR, 32);
        fields[4] = new Field("", "SUPERTYPE_SCHEM", Types.CHAR, 32);
        fields[5] = new Field("", "SUPERTYPE_NAME", Types.CHAR, 32);

        return buildResultSet(fields, new ArrayList());
    }

    /**
     * Get a comma separated list of system functions.
     *
     * @return the list
     *
     * @throws SQLException DOCUMENT ME!
     */
    public String getSystemFunctions() throws SQLException {
        return "DATABASE,USER,SYSTEM_USER,SESSION_USER,PASSWORD,ENCRYPT,LAST_INSERT_ID,VERSION";
    }

    /**
     * Get the table types available in this database.  The results are ordered
     * by table type.
     *
     * <P>
     * The table type is:
     *
     * <OL>
     * <li>
     * <B>TABLE_TYPE</B> String => table type.  Typical types are "TABLE",
     * "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS",
     * "SYNONYM".
     * </li>
     * </ol>
     * </p>
     *
     * @return ResultSet each row has a single String column that is a table
     *         type
     *
     * @throws SQLException DOCUMENT ME!
     */
    public java.sql.ResultSet getTableTypes() throws SQLException {
        ArrayList tuples = new ArrayList();
        Field[] fields = new Field[1];
        fields[0] = new Field("", "TABLE_TYPE", Types.VARCHAR, 5);

        byte[][] tableTypeRow = new byte[1][];
        tableTypeRow[0] = TABLE_AS_BYTES;
        tuples.add(tableTypeRow);

        if (this.conn.versionMeetsMinimum(5, 0, 1)) {
            byte[][] viewTypeRow = new byte[1][];
            viewTypeRow[0] = VIEW_AS_BYTES;
            tuples.add(viewTypeRow);
        }

        byte[][] tempTypeRow = new byte[1][];
        tempTypeRow[0] = s2b("LOCAL TEMPORARY");
        tuples.add(tempTypeRow);

        return buildResultSet(fields, tuples);
    }

    /**
     * Get a comma separated list of time and date functions.
     *
     * @return the list
     *
     * @throws SQLException DOCUMENT ME!
     */
    public String getTimeDateFunctions() throws SQLException {
        return "DAYOFWEEK,WEEKDAY,DAYOFMONTH,DAYOFYEAR,MONTH,DAYNAME," +
        "MONTHNAME,QUARTER,WEEK,YEAR,HOUR,MINUTE,SECOND,PERIOD_ADD," +
        "PERIOD_DIFF,TO_DAYS,FROM_DAYS,DATE_FORMAT,TIME_FORMAT," +
        "CURDATE,CURRENT_DATE,CURTIME,CURRENT_TIME,NOW,SYSDATE," +
        "CURRENT_TIMESTAMP,UNIX_TIMESTAMP,FROM_UNIXTIME," +
        "SEC_TO_TIME,TIME_TO_SEC";
    }

    /**
     * Get a description of all the standard SQL types supported by this
     * database. They are ordered by DATA_TYPE and then by how closely the
     * data type maps to the corresponding JDBC SQL type.
     *
     * <P>
     * Each type description has the following columns:
     *
     * <OL>
     * <li>
     * <B>TYPE_NAME</B> String => Type name
     * </li>
     * <li>
     * <B>DATA_TYPE</B> short => SQL data type from java.sql.Types
     * </li>
     * <li>
     * <B>PRECISION</B> int => maximum precision
     * </li>
     * <li>
     * <B>LITERAL_PREFIX</B> String => prefix used to quote a literal (may be
     * null)
     * </li>
     * <li>
     * <B>LITERAL_SUFFIX</B> String => suffix used to quote a literal (may be
     * null)
     * </li>
     * <li>
     * <B>CREATE_PARAMS</B> String => parameters used in creating the type (may
     * be null)
     * </li>
     * <li>
     * <B>NULLABLE</B> short => can you use NULL for this type?
     *
     * <UL>
     * <li>
     * typeNoNulls - does not allow NULL values
     * </li>
     * <li>
     * typeNullable - allows NULL values
     * </li>
     * <li>
     * typeNullableUnknown - nullability unknown
     * </li>
     * </ul>
     *
     * </li>
     * <li>
     * <B>CASE_SENSITIVE</B> boolean=> is it case sensitive?
     * </li>
     * <li>
     * <B>SEARCHABLE</B> short => can you use "WHERE" based on this type:
     *
     * <UL>
     * <li>
     * typePredNone - No support
     * </li>
     * <li>
     * typePredChar - Only supported with WHERE .. LIKE
     * </li>
     * <li>
     * typePredBasic - Supported except for WHERE .. LIKE
     * </li>
     * <li>
     * typeSearchable - Supported for all WHERE ..
     * </li>
     * </ul>
     *
     * </li>
     * <li>
     * <B>UNSIGNED_ATTRIBUTE</B> boolean => is it unsigned?
     * </li>
     * <li>
     * <B>FIXED_PREC_SCALE</B> boolean => can it be a money value?
     * </li>
     * <li>
     * <B>AUTO_INCREMENT</B> boolean => can it be used for an auto-increment
     * value?
     * </li>
     * <li>
     * <B>LOCAL_TYPE_NAME</B> String => localized version of type name (may be
     * null)
     * </li>
     * <li>
     * <B>MINIMUM_SCALE</B> short => minimum scale supported
     * </li>
     * <li>
     * <B>MAXIMUM_SCALE</B> short => maximum scale supported
     * </li>
     * <li>
     * <B>SQL_DATA_TYPE</B> int => unused
     * </li>
     * <li>
     * <B>SQL_DATETIME_SUB</B> int => unused
     * </li>
     * <li>
     * <B>NUM_PREC_RADIX</B> int => usually 2 or 10
     * </li>
     * </ol>
     * </p>
     *
     * @return ResultSet each row is a SQL type description
     *
     * @throws SQLException DOCUMENT ME!
     */
    /**
     * Get a description of all the standard SQL types supported by this
     * database. They are ordered by DATA_TYPE and then by how closely the
     * data type maps to the corresponding JDBC SQL type.
     *
     * <P>
     * Each type description has the following columns:
     *
     * <OL>
     * <li>
     * <B>TYPE_NAME</B> String => Type name
     * </li>
     * <li>
     * <B>DATA_TYPE</B> short => SQL data type from java.sql.Types
     * </li>
     * <li>
     * <B>PRECISION</B> int => maximum precision
     * </li>
     * <li>
     * <B>LITERAL_PREFIX</B> String => prefix used to quote a literal (may be
     * null)
     * </li>
     * <li>
     * <B>LITERAL_SUFFIX</B> String => suffix used to quote a literal (may be
     * null)
     * </li>
     * <li>
     * <B>CREATE_PARAMS</B> String => parameters used in creating the type (may
     * be null)
     * </li>
     * <li>
     * <B>NULLABLE</B> short => can you use NULL for this type?
     *
     * <UL>
     * <li>
     * typeNoNulls - does not allow NULL values
     * </li>
     * <li>
     * typeNullable - allows NULL values
     * </li>
     * <li>
     * typeNullableUnknown - nullability unknown
     * </li>
     * </ul>
     *
     * </li>
     * <li>
     * <B>CASE_SENSITIVE</B> boolean=> is it case sensitive?
     * </li>
     * <li>
     * <B>SEARCHABLE</B> short => can you use "WHERE" based on this type:
     *
     * <UL>
     * <li>
     * typePredNone - No support
     * </li>
     * <li>
     * typePredChar - Only supported with WHERE .. LIKE
     * </li>
     * <li>
     * typePredBasic - Supported except for WHERE .. LIKE
     * </li>
     * <li>
     * typeSearchable - Supported for all WHERE ..
     * </li>
     * </ul>
     *
     * </li>
     * <li>
     * <B>UNSIGNED_ATTRIBUTE</B> boolean => is it unsigned?
     * </li>
     * <li>
     * <B>FIXED_PREC_SCALE</B> boolean => can it be a money value?
     * </li>
     * <li>
     * <B>AUTO_INCREMENT</B> boolean => can it be used for an auto-increment
     * value?
     * </li>
     * <li>
     * <B>LOCAL_TYPE_NAME</B> String => localized version of type name (may be
     * null)
     * </li>
     * <li>
     * <B>MINIMUM_SCALE</B> short => minimum scale supported
     * </li>
     * <li>
     * <B>MAXIMUM_SCALE</B> short => maximum scale supported
     * </li>
     * <li>
     * <B>SQL_DATA_TYPE</B> int => unused
     * </li>
     * <li>
     * <B>SQL_DATETIME_SUB</B> int => unused
     * </li>
     * <li>
     * <B>NUM_PREC_RADIX</B> int => usually 2 or 10
     * </li>
     * </ol>
     * </p>
     *
     * @return ResultSet each row is a SQL type description
     *
     * @throws SQLException DOCUMENT ME!
     */
    public java.sql.ResultSet getTypeInfo() throws SQLException {
        Field[] fields = new Field[18];
        fields[0] = new Field("", "TYPE_NAME", Types.CHAR, 32);
        fields[1] = new Field("", "DATA_TYPE", Types.SMALLINT, 5);
        fields[2] = new Field("", "PRECISION", Types.INTEGER, 10);
        fields[3] = new Field("", "LITERAL_PREFIX", Types.CHAR, 4);
        fields[4] = new Field("", "LITERAL_SUFFIX", Types.CHAR, 4);
        fields[5] = new Field("", "CREATE_PARAMS", Types.CHAR, 32);
        fields[6] = new Field("", "NULLABLE", Types.SMALLINT, 5);
        fields[7] = new Field("", "CASE_SENSITIVE", Types.CHAR, 3);
        fields[8] = new Field("", "SEARCHABLE", Types.SMALLINT, 3);
        fields[9] = new Field("", "UNSIGNED_ATTRIBUTE", Types.CHAR, 3);
        fields[10] = new Field("", "FIXED_PREC_SCALE", Types.CHAR, 3);
        fields[11] = new Field("", "AUTO_INCREMENT", Types.CHAR, 3);
        fields[12] = new Field("", "LOCAL_TYPE_NAME", Types.CHAR, 32);
        fields[13] = new Field("", "MINIMUM_SCALE", Types.SMALLINT, 5);
        fields[14] = new Field("", "MAXIMUM_SCALE", Types.SMALLINT, 5);
        fields[15] = new Field("", "SQL_DATA_TYPE", Types.INTEGER, 10);
        fields[16] = new Field("", "SQL_DATETIME_SUB", Types.INTEGER, 10);
        fields[17] = new Field("", "NUM_PREC_RADIX", Types.INTEGER, 10);

        byte[][] rowVal = null;
        ArrayList tuples = new ArrayList();

        /*
         * The following are ordered by java.sql.Types, and
         * then by how closely the MySQL type matches the
         * JDBC Type (per spec)
         */
        /*
         * MySQL Type: BIT (silently converted to TINYINT(1))
         * JDBC  Type: BIT
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("BIT");
        rowVal[1] = Integer.toString(java.sql.Types.BIT).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("1"); // Precision
        rowVal[3] = s2b(""); // Literal Prefix
        rowVal[4] = s2b(""); // Literal Suffix
        rowVal[5] = s2b(""); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("true"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("BIT"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: BOOL (silently converted to TINYINT(1))
         * JDBC  Type: BIT
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("BOOL");
        rowVal[1] = Integer.toString(java.sql.Types.BIT).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("1"); // Precision
        rowVal[3] = s2b(""); // Literal Prefix
        rowVal[4] = s2b(""); // Literal Suffix
        rowVal[5] = s2b(""); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("true"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("BOOL"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: TINYINT
         * JDBC  Type: TINYINT
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("TINYINT");
        rowVal[1] = Integer.toString(java.sql.Types.TINYINT).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("3"); // Precision
        rowVal[3] = s2b(""); // Literal Prefix
        rowVal[4] = s2b(""); // Literal Suffix
        rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]"); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("true"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("true"); // Auto Increment
        rowVal[12] = s2b("TINYINT"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: BIGINT
         * JDBC  Type: BIGINT
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("BIGINT");
        rowVal[1] = Integer.toString(java.sql.Types.BIGINT).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("19"); // Precision
        rowVal[3] = s2b(""); // Literal Prefix
        rowVal[4] = s2b(""); // Literal Suffix
        rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]"); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("true"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("true"); // Auto Increment
        rowVal[12] = s2b("BIGINT"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: LONG VARBINARY
         * JDBC  Type: LONGVARBINARY
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("LONG VARBINARY");
        rowVal[1] = Integer.toString(java.sql.Types.LONGVARBINARY).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("16777215"); // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b(""); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("true"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("LONG VARBINARY"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: MEDIUMBLOB
         * JDBC  Type: LONGVARBINARY
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("MEDIUMBLOB");
        rowVal[1] = Integer.toString(java.sql.Types.LONGVARBINARY).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("16777215"); // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b(""); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("true"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("MEDIUMBLOB"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: LONGBLOB
         * JDBC  Type: LONGVARBINARY
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("LONGBLOB");
        rowVal[1] = Integer.toString(java.sql.Types.LONGVARBINARY).getBytes();

        // JDBC Data type
        rowVal[2] = Integer.toString(Integer.MAX_VALUE).getBytes();

        // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b(""); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("true"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("LONGBLOB"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: BLOB
         * JDBC  Type: LONGVARBINARY
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("BLOB");
        rowVal[1] = Integer.toString(java.sql.Types.LONGVARBINARY).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("65535"); // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b(""); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("true"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("BLOB"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: TINYBLOB
         * JDBC  Type: LONGVARBINARY
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("TINYBLOB");
        rowVal[1] = Integer.toString(java.sql.Types.LONGVARBINARY).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("255"); // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b(""); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("true"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("TINYBLOB"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: VARBINARY (sliently converted to VARCHAR(M) BINARY)
         * JDBC  Type: VARBINARY
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("VARBINARY");
        rowVal[1] = Integer.toString(java.sql.Types.VARBINARY).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("255"); // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b("(M)"); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("true"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("VARBINARY"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: BINARY (silently converted to CHAR(M) BINARY)
         * JDBC  Type: BINARY
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("BINARY");
        rowVal[1] = Integer.toString(java.sql.Types.BINARY).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("255"); // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b("(M)"); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("true"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("BINARY"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: LONG VARCHAR
         * JDBC  Type: LONGVARCHAR
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("LONG VARCHAR");
        rowVal[1] = Integer.toString(java.sql.Types.LONGVARCHAR).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("16777215"); // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b(""); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("LONG VARCHAR"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: MEDIUMTEXT
         * JDBC  Type: LONGVARCHAR
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("MEDIUMTEXT");
        rowVal[1] = Integer.toString(java.sql.Types.LONGVARCHAR).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("16777215"); // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b(""); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("MEDIUMTEXT"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: LONGTEXT
         * JDBC  Type: LONGVARCHAR
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("LONGTEXT");
        rowVal[1] = Integer.toString(java.sql.Types.LONGVARCHAR).getBytes();

        // JDBC Data type
        rowVal[2] = Integer.toString(Integer.MAX_VALUE).getBytes();

        // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b(""); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("LONGTEXT"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: TEXT
         * JDBC  Type: LONGVARCHAR
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("TEXT");
        rowVal[1] = Integer.toString(java.sql.Types.LONGVARCHAR).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("65535"); // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b(""); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("TEXT"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: TINYTEXT
         * JDBC  Type: LONGVARCHAR
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("TINYTEXT");
        rowVal[1] = Integer.toString(java.sql.Types.LONGVARCHAR).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("255"); // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b(""); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("TINYTEXT"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: CHAR
         * JDBC  Type: CHAR
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("CHAR");
        rowVal[1] = Integer.toString(java.sql.Types.CHAR).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("255"); // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b("(M)"); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("CHAR"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: NUMERIC (silently converted to DECIMAL)
         * JDBC  Type: NUMERIC
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("NUMERIC");
        rowVal[1] = Integer.toString(java.sql.Types.NUMERIC).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("17"); // Precision
        rowVal[3] = s2b(""); // Literal Prefix
        rowVal[4] = s2b(""); // Literal Suffix
        rowVal[5] = s2b("[(M[,D])] [ZEROFILL]"); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("true"); // Auto Increment
        rowVal[12] = s2b("NUMERIC"); // Locale Type Name
        rowVal[13] = s2b("-308"); // Minimum Scale
        rowVal[14] = s2b("308"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: DECIMAL
         * JDBC  Type: DECIMAL
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("DECIMAL");
        rowVal[1] = Integer.toString(java.sql.Types.DECIMAL).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("17"); // Precision
        rowVal[3] = s2b(""); // Literal Prefix
        rowVal[4] = s2b(""); // Literal Suffix
        rowVal[5] = s2b("[(M[,D])] [ZEROFILL]"); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("true"); // Auto Increment
        rowVal[12] = s2b("DECIMAL"); // Locale Type Name
        rowVal[13] = s2b("-308"); // Minimum Scale
        rowVal[14] = s2b("308"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: INTEGER
         * JDBC  Type: INTEGER
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("INTEGER");
        rowVal[1] = Integer.toString(java.sql.Types.INTEGER).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("10"); // Precision
        rowVal[3] = s2b(""); // Literal Prefix
        rowVal[4] = s2b(""); // Literal Suffix
        rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]"); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("true"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("true"); // Auto Increment
        rowVal[12] = s2b("INTEGER"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: INT
         * JDBC  Type: INTEGER
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("INT");
        rowVal[1] = Integer.toString(java.sql.Types.INTEGER).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("10"); // Precision
        rowVal[3] = s2b(""); // Literal Prefix
        rowVal[4] = s2b(""); // Literal Suffix
        rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]"); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("true"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("true"); // Auto Increment
        rowVal[12] = s2b("INT"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: MEDIUMINT
         * JDBC  Type: INTEGER
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("MEDIUMINT");
        rowVal[1] = Integer.toString(java.sql.Types.INTEGER).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("7"); // Precision
        rowVal[3] = s2b(""); // Literal Prefix
        rowVal[4] = s2b(""); // Literal Suffix
        rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]"); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("true"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("true"); // Auto Increment
        rowVal[12] = s2b("MEDIUMINT"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: SMALLINT
         * JDBC  Type: SMALLINT
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("SMALLINT");
        rowVal[1] = Integer.toString(java.sql.Types.SMALLINT).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("5"); // Precision
        rowVal[3] = s2b(""); // Literal Prefix
        rowVal[4] = s2b(""); // Literal Suffix
        rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]"); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("true"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("true"); // Auto Increment
        rowVal[12] = s2b("SMALLINT"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: FLOAT
         * JDBC  Type: REAL (this is the SINGLE PERCISION floating point type)
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("FLOAT");
        rowVal[1] = Integer.toString(java.sql.Types.REAL).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("10"); // Precision
        rowVal[3] = s2b(""); // Literal Prefix
        rowVal[4] = s2b(""); // Literal Suffix
        rowVal[5] = s2b("[(M,D)] [ZEROFILL]"); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("true"); // Auto Increment
        rowVal[12] = s2b("FLOAT"); // Locale Type Name
        rowVal[13] = s2b("-38"); // Minimum Scale
        rowVal[14] = s2b("38"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: DOUBLE
         * JDBC  Type: DOUBLE
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("DOUBLE");
        rowVal[1] = Integer.toString(java.sql.Types.DOUBLE).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("17"); // Precision
        rowVal[3] = s2b(""); // Literal Prefix
        rowVal[4] = s2b(""); // Literal Suffix
        rowVal[5] = s2b("[(M,D)] [ZEROFILL]"); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("true"); // Auto Increment
        rowVal[12] = s2b("DOUBLE"); // Locale Type Name
        rowVal[13] = s2b("-308"); // Minimum Scale
        rowVal[14] = s2b("308"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: DOUBLE PRECISION
         * JDBC  Type: DOUBLE
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("DOUBLE PRECISION");
        rowVal[1] = Integer.toString(java.sql.Types.DOUBLE).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("17"); // Precision
        rowVal[3] = s2b(""); // Literal Prefix
        rowVal[4] = s2b(""); // Literal Suffix
        rowVal[5] = s2b("[(M,D)] [ZEROFILL]"); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("true"); // Auto Increment
        rowVal[12] = s2b("DOUBLE PRECISION"); // Locale Type Name
        rowVal[13] = s2b("-308"); // Minimum Scale
        rowVal[14] = s2b("308"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: REAL (does not map to Types.REAL)
         * JDBC  Type: DOUBLE
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("REAL");
        rowVal[1] = Integer.toString(java.sql.Types.DOUBLE).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("17"); // Precision
        rowVal[3] = s2b(""); // Literal Prefix
        rowVal[4] = s2b(""); // Literal Suffix
        rowVal[5] = s2b("[(M,D)] [ZEROFILL]"); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("true"); // Auto Increment
        rowVal[12] = s2b("REAL"); // Locale Type Name
        rowVal[13] = s2b("-308"); // Minimum Scale
        rowVal[14] = s2b("308"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: VARCHAR
         * JDBC  Type: VARCHAR
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("VARCHAR");
        rowVal[1] = Integer.toString(java.sql.Types.VARCHAR).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("255"); // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b("(M)"); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("VARCHAR"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: ENUM
         * JDBC  Type: VARCHAR
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("ENUM");
        rowVal[1] = Integer.toString(java.sql.Types.VARCHAR).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("65535"); // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b(""); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("ENUM"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: SET
         * JDBC  Type: VARCHAR
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("SET");
        rowVal[1] = Integer.toString(java.sql.Types.VARCHAR).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("64"); // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b(""); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("SET"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: DATE
         * JDBC  Type: DATE
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("DATE");
        rowVal[1] = Integer.toString(java.sql.Types.DATE).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("0"); // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b(""); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("DATE"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: TIME
         * JDBC  Type: TIME
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("TIME");
        rowVal[1] = Integer.toString(java.sql.Types.TIME).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("0"); // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b(""); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("TIME"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: DATETIME
         * JDBC  Type: TIMESTAMP
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("DATETIME");
        rowVal[1] = Integer.toString(java.sql.Types.TIMESTAMP).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("0"); // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b(""); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("DATETIME"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        /*
         * MySQL Type: TIMESTAMP
         * JDBC  Type: TIMESTAMP
         */
        rowVal = new byte[18][];
        rowVal[0] = s2b("TIMESTAMP");
        rowVal[1] = Integer.toString(java.sql.Types.TIMESTAMP).getBytes();

        // JDBC Data type
        rowVal[2] = s2b("0"); // Precision
        rowVal[3] = s2b("'"); // Literal Prefix
        rowVal[4] = s2b("'"); // Literal Suffix
        rowVal[5] = s2b("[(M)]"); // Create Params
        rowVal[6] = Integer.toString(java.sql.DatabaseMetaData.typeNullable)
                           .getBytes();

        // Nullable
        rowVal[7] = s2b("false"); // Case Sensitive
        rowVal[8] = Integer.toString(java.sql.DatabaseMetaData.typeSearchable)
                           .getBytes();

        // Searchable
        rowVal[9] = s2b("false"); // Unsignable
        rowVal[10] = s2b("false"); // Fixed Prec Scale
        rowVal[11] = s2b("false"); // Auto Increment
        rowVal[12] = s2b("TIMESTAMP"); // Locale Type Name
        rowVal[13] = s2b("0"); // Minimum Scale
        rowVal[14] = s2b("0"); // Maximum Scale
        rowVal[15] = s2b("0"); // SQL Data Type (not used)
        rowVal[16] = s2b("0"); // SQL DATETIME SUB (not used)
        rowVal[17] = s2b("10"); //  NUM_PREC_RADIX (2 or 10)
        tuples.add(rowVal);

        return buildResultSet(fields, tuples);
    }

    /**
     * JDBC 2.0 Get a description of the user-defined types defined in a
     * particular schema.  Schema specific UDTs may have type JAVA_OBJECT,
     * STRUCT,  or DISTINCT.
     *
     * <P>
     * Only types matching the catalog, schema, type name and type   criteria
     * are returned.  They are ordered by DATA_TYPE, TYPE_SCHEM  and
     * TYPE_NAME.  The type name parameter may be a fully qualified  name. In
     * this case, the catalog and schemaPattern parameters are ignored.
     * </p>
     *
     * <P>
     * Each type description has the following columns:
     *
     * <OL>
     * <li>
     * <B>TYPE_CAT</B> String => the type's catalog (may be null)
     * </li>
     * <li>
     * <B>TYPE_SCHEM</B> String => type's schema (may be null)
     * </li>
     * <li>
     * <B>TYPE_NAME</B> String => type name
     * </li>
     * <li>
     * <B>CLASS_NAME</B> String => Java class name
     * </li>
     * <li>
     * <B>DATA_TYPE</B> String => type value defined in java.sql.Types.   One
     * of JAVA_OBJECT, STRUCT, or DISTINCT
     * </li>
     * <li>
     * <B>REMARKS</B> String => explanatory comment on the type
     * </li>
     * </ol>
     * </p>
     *
     * <P>
     * <B>Note:</B> If the driver does not support UDTs then an empty result
     * set is returned.
     * </p>
     *
     * @param catalog a catalog name; "" retrieves those without a catalog;
     *        null means drop catalog name from the selection criteria
     * @param schemaPattern a schema name pattern; "" retrieves those without a
     *        schema
     * @param typeNamePattern a type name pattern; may be a fully qualified
     *        name
     * @param types a list of user-named types to include (JAVA_OBJECT, STRUCT,
     *        or DISTINCT); null returns all types
     *
     * @return ResultSet - each row is a type description
     *
     * @exception SQLException if a database-access error occurs.
     */
    public java.sql.ResultSet getUDTs(String catalog, String schemaPattern,
        String typeNamePattern, int[] types) throws SQLException {
        Field[] fields = new Field[6];
        fields[0] = new Field("", "TYPE_CAT", Types.VARCHAR, 32);
        fields[1] = new Field("", "TYPE_SCHEM", Types.VARCHAR, 32);
        fields[2] = new Field("", "TYPE_NAME", Types.VARCHAR, 32);
        fields[3] = new Field("", "CLASS_NAME", Types.VARCHAR, 32);
        fields[4] = new Field("", "DATA_TYPE", Types.VARCHAR, 32);
        fields[5] = new Field("", "REMARKS", Types.VARCHAR, 32);

        ArrayList tuples = new ArrayList();

        return buildResultSet(fields, tuples);
    }

    /**
     * What's the url for this database?
     *
     * @return the url or null if it can't be generated
     *
     * @throws SQLException DOCUMENT ME!
     */
    public String getURL() throws SQLException {
        return this.conn.getURL();
    }

    /**
     * What's our user name as known to the database?
     *
     * @return our database user name
     *
     * @throws SQLException DOCUMENT ME!
     */
    public String getUserName() throws SQLException {
        if (this.conn.getUseHostsInPrivileges()) {
            Statement stmt = null;
            ResultSet rs = null;

            try {
                stmt = this.conn.createStatement();
                stmt.setEscapeProcessing(false);

                rs = stmt.executeQuery("SELECT USER()");
                rs.next();

                return rs.getString(1);
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (Exception ex) {
                        AssertionFailedException.shouldNotHappen(ex);
                    }

                    rs = null;
                }

                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (Exception ex) {
                        AssertionFailedException.shouldNotHappen(ex);
                    }

                    stmt = null;
                }
            }
        }

        return this.conn.getUser();
    }

    /**
     * Can all the procedures returned by getProcedures be called by the
     * current user?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean allProceduresAreCallable() throws SQLException {
        return false;
    }

    /**
     * Can all the tables returned by getTable be SELECTed by the current user?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean allTablesAreSelectable() throws SQLException {
        return false;
    }

    /**
     * Does a data definition statement within a transaction force the
     * transaction to commit?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean dataDefinitionCausesTransactionCommit()
        throws SQLException {
        return true;
    }

    /**
     * Is a data definition statement within a transaction ignored?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean dataDefinitionIgnoredInTransactions()
        throws SQLException {
        return false;
    }

    /**
     * JDBC 2.0 Determine whether or not a visible row delete can be detected
     * by  calling ResultSet.rowDeleted().  If deletesAreDetected() returns
     * false, then deleted rows are removed from the result set.
     *
     * @param type set type, i.e. ResultSet.TYPE_XXX
     *
     * @return true if changes are detected by the resultset type
     *
     * @exception SQLException if a database-access error occurs.
     */
    public boolean deletesAreDetected(int type) throws SQLException {
        return false;
    }

    /**
     * Did getMaxRowSize() include LONGVARCHAR and LONGVARBINARY blobs?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return true;
    }

    /**
     * JDBC 2.0 Determine whether or not a visible row insert can be detected
     * by calling ResultSet.rowInserted().
     *
     * @param type set type, i.e. ResultSet.TYPE_XXX
     *
     * @return true if changes are detected by the resultset type
     *
     * @exception SQLException if a database-access error occurs.
     */
    public boolean insertsAreDetected(int type) throws SQLException {
        return false;
    }

    /**
     * @see DatabaseMetaData#locatorsUpdateCopy()
     */
    public boolean locatorsUpdateCopy() throws SQLException {
        return !this.conn.getEmulateLocators();
    }

    /**
     * Are concatenations between NULL and non-NULL values NULL? A JDBC
     * compliant driver always returns true.
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean nullPlusNonNullIsNull() throws SQLException {
        return true;
    }

    /**
     * Are NULL values sorted at the end regardless of sort order?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean nullsAreSortedAtEnd() throws SQLException {
        return false;
    }

    /**
     * Are NULL values sorted at the start regardless of sort order?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean nullsAreSortedAtStart() throws SQLException {
        return (this.conn.versionMeetsMinimum(4, 0, 2) &&
        !this.conn.versionMeetsMinimum(4, 0, 11));
    }

    /**
     * Are NULL values sorted high?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean nullsAreSortedHigh() throws SQLException {
        return false;
    }

    /**
     * Are NULL values sorted low?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean nullsAreSortedLow() throws SQLException {
        return !nullsAreSortedHigh();
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean othersDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean othersInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    /**
     * JDBC 2.0 Determine whether changes made by others are visible.
     *
     * @param type set type, i.e. ResultSet.TYPE_XXX
     *
     * @return true if changes are visible for the result set type
     *
     * @exception SQLException if a database-access error occurs.
     */
    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean ownDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean ownInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    /**
     * JDBC 2.0 Determine whether a result set's own changes visible.
     *
     * @param type set type, i.e. ResultSet.TYPE_XXX
     *
     * @return true if changes are visible for the result set type
     *
     * @exception SQLException if a database-access error occurs.
     */
    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        return false;
    }

    /**
     * Does the database store mixed case unquoted SQL identifiers in lower
     * case?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return false;
    }

    /**
     * Does the database store mixed case quoted SQL identifiers in lower case?
     * A JDBC compliant driver will always return false.
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    /**
     * Does the database store mixed case unquoted SQL identifiers in mixed
     * case?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        return true;
    }

    /**
     * Does the database store mixed case quoted SQL identifiers in mixed case?
     * A JDBC compliant driver will always return false.
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    /**
     * Does the database store mixed case unquoted SQL identifiers in upper
     * case?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return false;
    }

    /**
     * Does the database store mixed case quoted SQL identifiers in upper case?
     * A JDBC compliant driver will always return true.
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    /**
     * Is the ANSI92 entry level SQL grammar supported? All JDBC compliant
     * drivers must return true.
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return true;
    }

    /**
     * Is the ANSI92 full SQL grammar supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsANSI92FullSQL() throws SQLException {
        return false;
    }

    /**
     * Is the ANSI92 intermediate SQL grammar supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return false;
    }

    /**
     * Is "ALTER TABLE" with add column supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return true;
    }

    /**
     * Is "ALTER TABLE" with drop column supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return true;
    }

    /**
     * JDBC 2.0 Return true if the driver supports batch updates, else return
     * false.
     *
     * @return DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsBatchUpdates() throws SQLException {
        return true;
    }

    /**
     * Can a catalog name be used in a data manipulation statement?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsCatalogsInDataManipulation()
        throws SQLException {
        // Servers before 3.22 could not do this
        return this.conn.versionMeetsMinimum(3, 22, 0);
    }

    /**
     * Can a catalog name be used in a index definition statement?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsCatalogsInIndexDefinitions()
        throws SQLException {
        // Servers before 3.22 could not do this
        return this.conn.versionMeetsMinimum(3, 22, 0);
    }

    /**
     * Can a catalog name be used in a privilege definition statement?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsCatalogsInPrivilegeDefinitions()
        throws SQLException {
        // Servers before 3.22 could not do this
        return this.conn.versionMeetsMinimum(3, 22, 0);
    }

    /**
     * Can a catalog name be used in a procedure call statement?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        // Servers before 3.22 could not do this
        return this.conn.versionMeetsMinimum(3, 22, 0);
    }

    /**
     * Can a catalog name be used in a table definition statement?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsCatalogsInTableDefinitions()
        throws SQLException {
        // Servers before 3.22 could not do this
        return this.conn.versionMeetsMinimum(3, 22, 0);
    }

    /**
     * Is column aliasing supported?
     *
     * <P>
     * If so, the SQL AS clause can be used to provide names for computed
     * columns or to provide alias names for columns as required. A JDBC
     * compliant driver always returns true.
     * </p>
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsColumnAliasing() throws SQLException {
        return true;
    }

    /**
     * Is the CONVERT function between SQL types supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsConvert() throws SQLException {
        return false;
    }

    /**
     * Is CONVERT between the given SQL types supported?
     *
     * @param fromType the type to convert from
     * @param toType the type to convert to
     *
     * @return true if so
     *
     * @throws SQLException if an error occurs
     *
     * @see Types
     */
    public boolean supportsConvert(int fromType, int toType)
        throws SQLException {
        switch (fromType) {
        /* The char/binary types can be converted
         * to pretty much anything.
         */
        case java.sql.Types.CHAR:
        case java.sql.Types.VARCHAR:
        case java.sql.Types.LONGVARCHAR:
        case java.sql.Types.BINARY:
        case java.sql.Types.VARBINARY:
        case java.sql.Types.LONGVARBINARY:

            switch (toType) {
            case java.sql.Types.DECIMAL:
            case java.sql.Types.NUMERIC:
            case java.sql.Types.REAL:
            case java.sql.Types.TINYINT:
            case java.sql.Types.SMALLINT:
            case java.sql.Types.INTEGER:
            case java.sql.Types.BIGINT:
            case java.sql.Types.FLOAT:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.CHAR:
            case java.sql.Types.VARCHAR:
            case java.sql.Types.LONGVARCHAR:
            case java.sql.Types.BINARY:
            case java.sql.Types.VARBINARY:
            case java.sql.Types.LONGVARBINARY:
            case java.sql.Types.OTHER:
            case java.sql.Types.DATE:
            case java.sql.Types.TIME:
            case java.sql.Types.TIMESTAMP:
                return true;

            default:
                return false;
            }

        /* We don't handle the BIT type
         * yet.
         */
        case java.sql.Types.BIT:
            return false;

        /* The numeric types. Basically they can convert
         * among themselves, and with char/binary types.
         */
        case java.sql.Types.DECIMAL:
        case java.sql.Types.NUMERIC:
        case java.sql.Types.REAL:
        case java.sql.Types.TINYINT:
        case java.sql.Types.SMALLINT:
        case java.sql.Types.INTEGER:
        case java.sql.Types.BIGINT:
        case java.sql.Types.FLOAT:
        case java.sql.Types.DOUBLE:

            switch (toType) {
            case java.sql.Types.DECIMAL:
            case java.sql.Types.NUMERIC:
            case java.sql.Types.REAL:
            case java.sql.Types.TINYINT:
            case java.sql.Types.SMALLINT:
            case java.sql.Types.INTEGER:
            case java.sql.Types.BIGINT:
            case java.sql.Types.FLOAT:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.CHAR:
            case java.sql.Types.VARCHAR:
            case java.sql.Types.LONGVARCHAR:
            case java.sql.Types.BINARY:
            case java.sql.Types.VARBINARY:
            case java.sql.Types.LONGVARBINARY:
                return true;

            default:
                return false;
            }

        /* MySQL doesn't support a NULL type. */
        case java.sql.Types.NULL:
            return false;

        /* With this driver, this will always be a serialized
         * object, so the char/binary types will work.
         */
        case java.sql.Types.OTHER:

            switch (toType) {
            case java.sql.Types.CHAR:
            case java.sql.Types.VARCHAR:
            case java.sql.Types.LONGVARCHAR:
            case java.sql.Types.BINARY:
            case java.sql.Types.VARBINARY:
            case java.sql.Types.LONGVARBINARY:
                return true;

            default:
                return false;
            }

        /* Dates can be converted to char/binary types. */
        case java.sql.Types.DATE:

            switch (toType) {
            case java.sql.Types.CHAR:
            case java.sql.Types.VARCHAR:
            case java.sql.Types.LONGVARCHAR:
            case java.sql.Types.BINARY:
            case java.sql.Types.VARBINARY:
            case java.sql.Types.LONGVARBINARY:
                return true;

            default:
                return false;
            }

        /* Time can be converted to char/binary types */
        case java.sql.Types.TIME:

            switch (toType) {
            case java.sql.Types.CHAR:
            case java.sql.Types.VARCHAR:
            case java.sql.Types.LONGVARCHAR:
            case java.sql.Types.BINARY:
            case java.sql.Types.VARBINARY:
            case java.sql.Types.LONGVARBINARY:
                return true;

            default:
                return false;
            }

        /* Timestamp can be converted to char/binary types
         * and date/time types (with loss of precision).
         */
        case java.sql.Types.TIMESTAMP:

            switch (toType) {
            case java.sql.Types.CHAR:
            case java.sql.Types.VARCHAR:
            case java.sql.Types.LONGVARCHAR:
            case java.sql.Types.BINARY:
            case java.sql.Types.VARBINARY:
            case java.sql.Types.LONGVARBINARY:
            case java.sql.Types.TIME:
            case java.sql.Types.DATE:
                return true;

            default:
                return false;
            }

        /* We shouldn't get here! */
        default:
            return false; // not sure
        }
    }

    /**
     * Is the ODBC Core SQL grammar supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsCoreSQLGrammar() throws SQLException {
        return true;
    }

    /**
     * Are correlated subqueries supported? A JDBC compliant driver always
     * returns true.
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return false;
    }

    /**
     * Are both data definition and data manipulation statements within a
     * transaction supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsDataDefinitionAndDataManipulationTransactions()
        throws SQLException {
        return false;
    }

    /**
     * Are only data manipulation statements within a transaction supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsDataManipulationTransactionsOnly()
        throws SQLException {
        return false;
    }

    /**
     * If table correlation names are supported, are they restricted to be
     * different from the names of the tables? A JDBC compliant driver always
     * returns true.
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsDifferentTableCorrelationNames()
        throws SQLException {
        return true;
    }

    /**
     * Are expressions in "ORDER BY" lists supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return true;
    }

    /**
     * Is the ODBC Extended SQL grammar supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return false;
    }

    /**
     * Are full nested outer joins supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsFullOuterJoins() throws SQLException {
        return false;
    }

    /**
     * JDBC 3.0
     *
     * @return DOCUMENT ME!
     */
    public boolean supportsGetGeneratedKeys() {
        return true;
    }

    /**
     * Is some form of "GROUP BY" clause supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsGroupBy() throws SQLException {
        return true;
    }

    /**
     * Can a "GROUP BY" clause add columns not in the SELECT provided it
     * specifies all the columns in the SELECT?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return true;
    }

    /**
     * Can a "GROUP BY" clause use columns not in the SELECT?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsGroupByUnrelated() throws SQLException {
        return false;
    }

    /**
     * Is the SQL Integrity Enhancement Facility supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsIntegrityEnhancementFacility()
        throws SQLException {
        return false;
    }

    /**
     * Is the escape character in "LIKE" clauses supported? A JDBC compliant
     * driver always returns true.
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsLikeEscapeClause() throws SQLException {
        return true;
    }

    /**
     * Is there limited support for outer joins?  (This will be true if
     * supportFullOuterJoins is true.)
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsLimitedOuterJoins() throws SQLException {
        return true;
    }

    /**
     * Is the ODBC Minimum SQL grammar supported? All JDBC compliant drivers
     * must return true.
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        return true;
    }

    /**
     * Does the database support mixed case unquoted SQL identifiers?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        return false;
    }

    /**
     * Does the database support mixed case quoted SQL identifiers? A JDBC
     * compliant driver will always return true.
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsMixedCaseQuotedIdentifiers()
        throws SQLException {
        return false;
    }

    /**
     * @see DatabaseMetaData#supportsMultipleOpenResults()
     */
    public boolean supportsMultipleOpenResults() throws SQLException {
        return false;
    }

    /**
     * Are multiple ResultSets from a single execute supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsMultipleResultSets() throws SQLException {
        return false;
    }

    /**
     * Can we have multiple transactions open at once (on different
     * connections)?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsMultipleTransactions() throws SQLException {
        return true;
    }

    /**
     * @see DatabaseMetaData#supportsNamedParameters()
     */
    public boolean supportsNamedParameters() throws SQLException {
        return false;
    }

    /**
     * Can columns be defined as non-nullable? A JDBC compliant driver always
     * returns true.
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsNonNullableColumns() throws SQLException {
        return true;
    }

    /**
     * Can cursors remain open across commits?
     *
     * @return true if so
     *
     * @throws SQLException if a database access error occurs
     *
     * @see Connection#disableAutoClose
     */
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return false;
    }

    /**
     * Can cursors remain open across rollbacks?
     *
     * @return true if so
     *
     * @throws SQLException if an error occurs
     *
     * @see Connection#disableAutoClose
     */
    public boolean supportsOpenCursorsAcrossRollback()
        throws SQLException {
        return false;
    }

    /**
     * Can statements remain open across commits?
     *
     * @return true if so
     *
     * @throws SQLException if an error occurs
     *
     * @see Connection#disableAutoClose
     */
    public boolean supportsOpenStatementsAcrossCommit()
        throws SQLException {
        return false;
    }

    /**
     * Can statements remain open across rollbacks?
     *
     * @return true if so
     *
     * @throws SQLException if an error occurs
     *
     * @see Connection#disableAutoClose
     */
    public boolean supportsOpenStatementsAcrossRollback()
        throws SQLException {
        return false;
    }

    /**
     * Can an "ORDER BY" clause use columns not in the SELECT?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsOrderByUnrelated() throws SQLException {
        return false;
    }

    /**
     * Is some form of outer join supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsOuterJoins() throws SQLException {
        return true;
    }

    /**
     * Is positioned DELETE supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsPositionedDelete() throws SQLException {
        return false;
    }

    /**
     * Is positioned UPDATE supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsPositionedUpdate() throws SQLException {
        return false;
    }

    /**
     * JDBC 2.0 Does the database support the concurrency type in combination
     * with the given result set type?
     *
     * @param type defined in java.sql.ResultSet
     * @param concurrency type defined in java.sql.ResultSet
     *
     * @return true if so
     *
     * @exception SQLException if a database-access error occurs.
     *
     * @see Connection
     */
    public boolean supportsResultSetConcurrency(int type, int concurrency)
        throws SQLException {
        return ((type == ResultSet.TYPE_SCROLL_INSENSITIVE) &&
        ((concurrency == ResultSet.CONCUR_READ_ONLY) ||
        (concurrency == ResultSet.CONCUR_UPDATABLE)));
    }

    /**
     * @see DatabaseMetaData#supportsResultSetHoldability(int)
     */
    public boolean supportsResultSetHoldability(int holdability)
        throws SQLException {
        return (holdability == ResultSet.HOLD_CURSORS_OVER_COMMIT);
    }

    /**
     * JDBC 2.0 Does the database support the given result set type?
     *
     * @param type defined in java.sql.ResultSet
     *
     * @return true if so
     *
     * @exception SQLException if a database-access error occurs.
     *
     * @see Connection
     */
    public boolean supportsResultSetType(int type) throws SQLException {
        return (type == ResultSet.TYPE_SCROLL_INSENSITIVE);
    }

    /**
     * @see DatabaseMetaData#supportsSavepoints()
     */
    public boolean supportsSavepoints() throws SQLException {
        
        return (this.conn.versionMeetsMinimum(4, 0, 14) ||
        		this.conn.versionMeetsMinimum(4, 1, 1));
    }

    /**
     * Can a schema name be used in a data manipulation statement?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsSchemasInDataManipulation()
        throws SQLException {
        return false;
    }

    /**
     * Can a schema name be used in an index definition statement?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsSchemasInIndexDefinitions()
        throws SQLException {
        return false;
    }

    /**
     * Can a schema name be used in a privilege definition statement?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsSchemasInPrivilegeDefinitions()
        throws SQLException {
        return false;
    }

    /**
     * Can a schema name be used in a procedure call statement?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return false;
    }

    /**
     * Can a schema name be used in a table definition statement?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsSchemasInTableDefinitions()
        throws SQLException {
        return false;
    }

    /**
     * Is SELECT for UPDATE supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsSelectForUpdate() throws SQLException {
        return false;
    }

    /**
     * @see DatabaseMetaData#supportsStatementPooling()
     */
    public boolean supportsStatementPooling() throws SQLException {
        return false;
    }

    /**
     * Are stored procedure calls using the stored procedure escape syntax
     * supported?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsStoredProcedures() throws SQLException {
        return this.conn.versionMeetsMinimum(5, 0, 0);
    }

    /**
     * Are subqueries in comparison expressions supported? A JDBC compliant
     * driver always returns true.
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return this.conn.versionMeetsMinimum(4, 1, 0);
    }

    /**
     * Are subqueries in exists expressions supported? A JDBC compliant driver
     * always returns true.
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsSubqueriesInExists() throws SQLException {
        return this.conn.versionMeetsMinimum(4, 1, 0);
    }

    /**
     * Are subqueries in "in" statements supported? A JDBC compliant driver
     * always returns true.
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsSubqueriesInIns() throws SQLException {
        return this.conn.versionMeetsMinimum(4, 1, 0);
    }

    /**
     * Are subqueries in quantified expressions supported? A JDBC compliant
     * driver always returns true.
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return this.conn.versionMeetsMinimum(4, 1, 0);
    }

    /**
     * Are table correlation names supported? A JDBC compliant driver always
     * returns true.
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsTableCorrelationNames() throws SQLException {
        return true;
    }

    /**
     * Does the database support the given transaction isolation level?
     *
     * @param level the values are defined in java.sql.Connection
     *
     * @return true if so
     *
     * @throws SQLException if a database access error occurs
     *
     * @see Connection
     */
    public boolean supportsTransactionIsolationLevel(int level)
        throws SQLException {
        if (this.conn.supportsIsolationLevel()) {
            switch (level) {
            case java.sql.Connection.TRANSACTION_READ_COMMITTED:
            case java.sql.Connection.TRANSACTION_READ_UNCOMMITTED:
            case java.sql.Connection.TRANSACTION_REPEATABLE_READ:
            case java.sql.Connection.TRANSACTION_SERIALIZABLE:
                return true;

            default:
                return false;
            }
        }

        return false;
    }

    /**
     * Are transactions supported? If not, commit is a noop and the isolation
     * level is TRANSACTION_NONE.
     *
     * @return true if transactions are supported
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsTransactions() throws SQLException {
        return this.conn.supportsTransactions();
    }

    /**
     * Is SQL UNION supported? A JDBC compliant driver always returns true.
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsUnion() throws SQLException {
        return this.conn.versionMeetsMinimum(4, 0, 0);
    }

    /**
     * Is SQL UNION ALL supported? A JDBC compliant driver always returns true.
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean supportsUnionAll() throws SQLException {
        return this.conn.versionMeetsMinimum(4, 0, 0);
    }

    /**
     * JDBC 2.0 Determine whether or not a visible row update can be detected
     * by  calling ResultSet.rowUpdated().
     *
     * @param type set type, i.e. ResultSet.TYPE_XXX
     *
     * @return true if changes are detected by the resultset type
     *
     * @exception SQLException if a database-access error occurs.
     */
    public boolean updatesAreDetected(int type) throws SQLException {
        return false;
    }

    /**
     * Does the database use a file for each table?
     *
     * @return true if the database uses a local file for each table
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean usesLocalFilePerTable() throws SQLException {
        return false;
    }

    /**
     * Does the database store tables in a local file?
     *
     * @return true if so
     *
     * @throws SQLException DOCUMENT ME!
     */
    public boolean usesLocalFiles() throws SQLException {
        return false;
    }

    private java.sql.ResultSet buildResultSet(com.mysql.jdbc.Field[] fields,
        java.util.ArrayList rows) throws SQLException {
        int fieldsLength = fields.length;

        for (int i = 0; i < fieldsLength; i++) {
            fields[i].setConnection(this.conn);
        }

        return new com.mysql.jdbc.ResultSet(this.conn.getCatalog(), fields,
            new InMemoryRowProvider(rows), this.conn, null);
    }

    /**
     * Converts the given string to bytes, using the connection's character
     * encoding, or if not available, the JVM default encoding.
     *
     * @param s DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private byte[] s2b(String s) {
        if ((this.conn != null) && this.conn.getUseUnicode()) {
            try {
                String encoding = this.conn.getEncoding();

                if (encoding == null) {
                    return s.getBytes();
                }

                SingleByteCharsetConverter converter = this.conn.getCharsetConverter(encoding);

                if (converter != null) {
                    return converter.toBytes(s);
                }

                return s.getBytes(encoding);
            } catch (java.io.UnsupportedEncodingException E) {
                return s.getBytes();
            }
        }

        return s.getBytes();
    }
}
