/*
 * Created on Nov 9, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
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
 * @author mmatthew
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
class SchemaDataUsingShow implements SchemaData {

	private Connection conn;
	/**
	 * The 'current' database name being used 
	 */
	private String database = null;
	/**
	 * What character to use when quoting identifiers 
	 */
	private String quotedId = null;
	private boolean supportsStoredProcedures;
	private static final int DEFERRABILITY = 13;
	private static final int DELETE_RULE = 10;
	private static final int FK_NAME = 11;
	private static final int FKCOLUMN_NAME = 7;
	private static final int FKTABLE_CAT = 4;
	private static final int FKTABLE_NAME = 6;
	private static final int FKTABLE_SCHEM = 5;
	private static final int KEY_SEQ = 8;
	private static final int PK_NAME = 12;
	private static final int PKCOLUMN_NAME = 3;
	private static final int PKTABLE_CAT = 0;
	private static final int PKTABLE_NAME = 2;
	private static final int PKTABLE_SCHEM = 1;
	/**
	 * The table type for generic tables that support foreign keys. 
	 */
	private static final String SUPPORTS_FK = "SUPPORTS_FK";
	private static final byte[] TABLE_AS_BYTES = "TABLE".getBytes();
	private static final int UPDATE_RULE = 9;
	private static final byte[] VIEW_AS_BYTES = "VIEW".getBytes();
	
	SchemaDataUsingShow(Connection conn, String database, String quotedId) throws SQLException {
		this.conn = conn;
		this.database = database;
		this.quotedId = quotedId;
		// Fixme: This should be passed in
		this.supportsStoredProcedures = this.conn.versionMeetsMinimum(5, 0, 0);
	}
	
	/**
	 * Get a description of the access rights for a table's columns.
	 *
	 * <P>
	 * Only privileges matching the column name criteria are returned.  They
	 * are ordered by COLUMN_NAME and PRIVILEGE.
	 * </p>
	 *
	 * <P>
	 * Each privilige description has the following columns:
	 *
	 * <OL>
	 * <li>
	 * <B>TABLE_CAT</B> String => table catalog (may be null)
	 * </li>
	 * <li>
	 * <B>TABLE_SCHEM</B> String => table schema (may be null)
	 * </li>
	 * <li>
	 * <B>TABLE_NAME</B> String => table name
	 * </li>
	 * <li>
	 * <B>COLUMN_NAME</B> String => column name
	 * </li>
	 * <li>
	 * <B>GRANTOR</B> => grantor of access (may be null)
	 * </li>
	 * <li>
	 * <B>GRANTEE</B> String => grantee of access
	 * </li>
	 * <li>
	 * <B>PRIVILEGE</B> String => name of access (SELECT, INSERT, UPDATE,
	 * REFRENCES, ...)
	 * </li>
	 * <li>
	 * <B>IS_GRANTABLE</B> String => "YES" if grantee is permitted to grant to
	 * others; "NO" if not; null if unknown
	 * </li>
	 * </ol>
	 * </p>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog
	 * @param schema a schema name; "" retrieves those without a schema
	 * @param table a table name
	 * @param columnNamePattern a column name pattern
	 *
	 * @return ResultSet each row is a column privilege description
	 *
	 * @throws SQLException if a database access error occurs
	 *
	 * @see #getSearchStringEscape
	 */
	public java.sql.ResultSet getColumnPrivileges(String catalog,
	    String schema, String table, String columnNamePattern)
	    throws SQLException {
	    Field[] fields = new Field[8];
	    fields[0] = new Field("", "TABLE_CAT", Types.CHAR, 64);
	    fields[1] = new Field("", "TABLE_SCHEM", Types.CHAR, 1);
	    fields[2] = new Field("", "TABLE_NAME", Types.CHAR, 64);
	    fields[3] = new Field("", "COLUMN_NAME", Types.CHAR, 64);
	    fields[4] = new Field("", "GRANTOR", Types.CHAR, 77);
	    fields[5] = new Field("", "GRANTEE", Types.CHAR, 77);
	    fields[6] = new Field("", "PRIVILEGE", Types.CHAR, 64);
	    fields[7] = new Field("", "IS_GRANTABLE", Types.CHAR, 3);
	
	    StringBuffer grantQuery = new StringBuffer(
	            "SELECT c.host, c.db, t.grantor, c.user, " +
	            "c.table_name, c.column_name, c.column_priv " +
	            "from mysql.columns_priv c, mysql.tables_priv t " +
	            "where c.host = t.host and c.db = t.db and " +
	            "c.table_name = t.table_name ");
	
	    if ((catalog != null) && (catalog.length() != 0)) {
	        grantQuery.append(" AND c.db='");
	        grantQuery.append(catalog);
	        grantQuery.append("' ");
	        ;
	    }
	
	    grantQuery.append(" AND c.table_name ='");
	    grantQuery.append(table);
	    grantQuery.append("' AND c.column_name like '");
	    grantQuery.append(columnNamePattern);
	    grantQuery.append("'");
	
	    Statement stmt = null;
	    ResultSet results = null;
	    ArrayList grantRows = new ArrayList();
	
	    try {
	        stmt = this.conn.createStatement();
	        stmt.setEscapeProcessing(false);
	        results = stmt.executeQuery(grantQuery.toString());
	
	        while (results.next()) {
	            String host = results.getString(1);
	            String db = results.getString(2);
	            String grantor = results.getString(3);
	            String user = results.getString(4);
	
	            if ((user == null) || (user.length() == 0)) {
	                user = "%";
	            }
	
	            StringBuffer fullUser = new StringBuffer(user);
	
	            if ((host != null) && this.conn.getUseHostsInPrivileges()) {
	                fullUser.append("@");
	                fullUser.append(host);
	            }
	
	            String columnName = results.getString(6);
	            String allPrivileges = results.getString(7);
	
	            if (allPrivileges != null) {
	                allPrivileges = allPrivileges.toUpperCase(Locale.ENGLISH);
	
	                StringTokenizer st = new StringTokenizer(allPrivileges, ",");
	
	                while (st.hasMoreTokens()) {
	                    String privilege = st.nextToken().trim();
	                    byte[][] tuple = new byte[8][];
	                    tuple[0] = s2b(db);
	                    tuple[1] = null;
	                    tuple[2] = s2b(table);
	                    tuple[3] = s2b(columnName);
	
	                    if (grantor != null) {
	                        tuple[4] = s2b(grantor);
	                    } else {
	                        tuple[4] = null;
	                    }
	
	                    tuple[5] = s2b(fullUser.toString());
	                    tuple[6] = s2b(privilege);
	                    tuple[7] = null;
	                    grantRows.add(tuple);
	                }
	            }
	        }
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
	
	    return buildResultSet(fields, grantRows);
	}

	/**
	 * Get a description of table columns available in a catalog.
	 *
	 * <P>
	 * Only column descriptions matching the catalog, schema, table and column
	 * name criteria are returned.  They are ordered by TABLE_SCHEM,
	 * TABLE_NAME and ORDINAL_POSITION.
	 * </p>
	 *
	 * <P>
	 * Each column description has the following columns:
	 *
	 * <OL>
	 * <li>
	 * <B>TABLE_CAT</B> String => table catalog (may be null)
	 * </li>
	 * <li>
	 * <B>TABLE_SCHEM</B> String => table schema (may be null)
	 * </li>
	 * <li>
	 * <B>TABLE_NAME</B> String => table name
	 * </li>
	 * <li>
	 * <B>COLUMN_NAME</B> String => column name
	 * </li>
	 * <li>
	 * <B>DATA_TYPE</B> short => SQL type from java.sql.Types
	 * </li>
	 * <li>
	 * <B>TYPE_NAME</B> String => Data source dependent type name
	 * </li>
	 * <li>
	 * <B>COLUMN_SIZE</B> int => column size.  For char or date types this is
	 * the maximum number of characters, for numeric or decimal types this is
	 * precision.
	 * </li>
	 * <li>
	 * <B>BUFFER_LENGTH</B> is not used.
	 * </li>
	 * <li>
	 * <B>DECIMAL_DIGITS</B> int => the number of fractional digits
	 * </li>
	 * <li>
	 * <B>NUM_PREC_RADIX</B> int => Radix (typically either 10 or 2)
	 * </li>
	 * <li>
	 * <B>NULLABLE</B> int => is NULL allowed?
	 *
	 * <UL>
	 * <li>
	 * columnNoNulls - might not allow NULL values
	 * </li>
	 * <li>
	 * columnNullable - definitely allows NULL values
	 * </li>
	 * <li>
	 * columnNullableUnknown - nullability unknown
	 * </li>
	 * </ul>
	 *
	 * </li>
	 * <li>
	 * <B>REMARKS</B> String => comment describing column (may be null)
	 * </li>
	 * <li>
	 * <B>COLUMN_DEF</B> String => default value (may be null)
	 * </li>
	 * <li>
	 * <B>SQL_DATA_TYPE</B> int => unused
	 * </li>
	 * <li>
	 * <B>SQL_DATETIME_SUB</B> int => unused
	 * </li>
	 * <li>
	 * <B>CHAR_OCTET_LENGTH</B> int => for char types the maximum number of
	 * bytes in the column
	 * </li>
	 * <li>
	 * <B>ORDINAL_POSITION</B> int => index of column in table (starting at 1)
	 * </li>
	 * <li>
	 * <B>IS_NULLABLE</B> String => "NO" means column definitely does not allow
	 * NULL values; "YES" means the column might allow NULL values.  An empty
	 * string means nobody knows.
	 * </li>
	 * </ol>
	 * </p>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog
	 * @param schemaPattern a schema name pattern; "" retrieves those without a
	 *        schema
	 * @param tableName a table name pattern
	 * @param columnNamePattern a column name pattern
	 *
	 * @return ResultSet each row is a column description
	 *
	 * @throws SQLException if a database access error occurs
	 *
	 * @see #getSearchStringEscape
	 */
	public java.sql.ResultSet getColumns(String catalog, String schemaPattern,
	    String tableName, String columnNamePattern) throws SQLException {
	    String databasePart = "";
	
	    if (columnNamePattern == null) {
	        columnNamePattern = "%";
	    }
	
	    if (catalog != null) {
	        if (!catalog.equals("")) {
	            databasePart = " FROM " + this.quotedId + catalog +
	                this.quotedId;
	        }
	    } else {
	        databasePart = " FROM " + this.quotedId + this.database +
	            this.quotedId;
	    }
	
	    ArrayList tableNameList = new ArrayList();
	    int tablenameLength = 0;
	
	    if (tableName == null) {
	        // Select from all tables
	        java.sql.ResultSet tables = null;
	
	        try {
	            tables = getTables(catalog, schemaPattern, "%", new String[0]);
	
	            while (tables.next()) {
	                String tableNameFromList = tables.getString("TABLE_NAME");
	                tableNameList.add(tableNameFromList);
	
	                if (tableNameFromList.length() > tablenameLength) {
	                    tablenameLength = tableNameFromList.length();
	                }
	            }
	        } finally {
	            if (tables != null) {
	                try {
	                    tables.close();
	                } catch (Exception sqlEx) {
	                    AssertionFailedException.shouldNotHappen(sqlEx);
	                }
	
	                tables = null;
	            }
	        }
	    } else {
	        java.sql.ResultSet tables = null;
	
	        try {
	            tables = getTables(catalog, schemaPattern, tableName,
	                    new String[0]);
	
	            while (tables.next()) {
	                String tableNameFromList = tables.getString("TABLE_NAME");
	                tableNameList.add(tableNameFromList);
	
	                if (tableNameFromList.length() > tablenameLength) {
	                    tablenameLength = tableNameFromList.length();
	                }
	            }
	        } finally {
	            if (tables != null) {
	                try {
	                    tables.close();
	                } catch (SQLException sqlEx) {
	                    AssertionFailedException.shouldNotHappen(sqlEx);
	                }
	
	                tables = null;
	            }
	        }
	    }
	
	    int catalogLength = 0;
	
	    if (catalog != null) {
	        catalogLength = catalog.length();
	    } else {
	        catalog = "";
	        catalogLength = 0;
	    }
	
	    java.util.Iterator tableNames = tableNameList.iterator();
	    Field[] fields = new Field[18];
	    fields[0] = new Field("", "TABLE_CAT", Types.CHAR, catalogLength);
	    fields[1] = new Field("", "TABLE_SCHEM", Types.CHAR, 0);
	    fields[2] = new Field("", "TABLE_NAME", Types.CHAR, tablenameLength);
	    fields[3] = new Field("", "COLUMN_NAME", Types.CHAR, 32);
	    fields[4] = new Field("", "DATA_TYPE", Types.SMALLINT, 5);
	    fields[5] = new Field("", "TYPE_NAME", Types.CHAR, 16);
	    fields[6] = new Field("", "COLUMN_SIZE", Types.INTEGER,
	            Integer.toString(Integer.MAX_VALUE).length());
	    fields[7] = new Field("", "BUFFER_LENGTH", Types.INTEGER, 10);
	    fields[8] = new Field("", "DECIMAL_DIGITS", Types.INTEGER, 10);
	    fields[9] = new Field("", "NUM_PREC_RADIX", Types.INTEGER, 10);
	    fields[10] = new Field("", "NULLABLE", Types.INTEGER, 10);
	    fields[11] = new Field("", "REMARKS", Types.CHAR, 0);
	    fields[12] = new Field("", "COLUMN_DEF", Types.CHAR, 0);
	    fields[13] = new Field("", "SQL_DATA_TYPE", Types.INTEGER, 10);
	    fields[14] = new Field("", "SQL_DATETIME_SUB", Types.INTEGER, 10);
	    fields[15] = new Field("", "CHAR_OCTET_LENGTH", Types.INTEGER,
	            Integer.toString(Integer.MAX_VALUE).length());
	    fields[16] = new Field("", "ORDINAL_POSITION", Types.INTEGER, 10);
	    fields[17] = new Field("", "IS_NULLABLE", Types.CHAR, 3);
	
	    ArrayList tuples = new ArrayList();
	
	    while (tableNames.hasNext()) {
	        String tableNamePattern = (String) tableNames.next();
	        Statement stmt = null;
	        ResultSet results = null;
	
	        try {
	            stmt = this.conn.createStatement();
	            stmt.setEscapeProcessing(false);
	
	            StringBuffer queryBuf = new StringBuffer("SHOW ");
	
	            if (this.conn.versionMeetsMinimum(4, 1, 0)) {
	                queryBuf.append("FULL ");
	            }
	
	            queryBuf.append("COLUMNS FROM ");
	            queryBuf.append(this.quotedId);
	            queryBuf.append(tableNamePattern);
	            queryBuf.append(this.quotedId);
	            queryBuf.append(databasePart);
	            queryBuf.append(" LIKE '");
	            queryBuf.append(columnNamePattern);
	            queryBuf.append("'");
	
	            // Return correct ordinals if column name pattern is
	            // not '%'
	            // Currently, MySQL doesn't show enough data to do
	            // this, so we do it the 'hard' way...Once _SYSTEM
	            // tables are in, this should be much easier
	            boolean fixUpOrdinalsRequired = false;
	            Map ordinalFixUpMap = null;
	
	            if (!columnNamePattern.equals("%")) {
	                fixUpOrdinalsRequired = true;
	
	                StringBuffer fullColumnQueryBuf = new StringBuffer("SHOW ");
	
	                if (this.conn.versionMeetsMinimum(4, 1, 0)) {
	                    fullColumnQueryBuf.append("FULL ");
	                }
	
	                fullColumnQueryBuf.append("COLUMNS FROM ");
	                fullColumnQueryBuf.append(this.quotedId);
	                fullColumnQueryBuf.append(tableNamePattern);
	                fullColumnQueryBuf.append(this.quotedId);
	                fullColumnQueryBuf.append(databasePart);
	
	                results = stmt.executeQuery(fullColumnQueryBuf.toString());
	
	                ordinalFixUpMap = new HashMap();
	
	                int fullOrdinalPos = 1;
	
	                while (results.next()) {
	                    String fullOrdColName = results.getString("Field");
	
	                    ordinalFixUpMap.put(fullOrdColName,
	                        new Integer(fullOrdinalPos++));
	                }
	            }
	
	            results = stmt.executeQuery(queryBuf.toString());
	
	            int ordPos = 1;
	
	            while (results.next()) {
	                byte[][] rowVal = new byte[18][];
	                rowVal[0] = s2b(catalog); // TABLE_CAT
	                rowVal[1] = null; // TABLE_SCHEM (No schemas in MySQL)
	
	                rowVal[2] = s2b(tableNamePattern); // TABLE_NAME
	                rowVal[3] = results.getBytes("Field");
	
	                TypeDescriptor typeDesc = new TypeDescriptor(this.conn, results.getString(
	                            "Type"), results.getString("Null"));
	
	                rowVal[4] = Short.toString(typeDesc.dataType).getBytes();
	
	                // DATA_TYPE (jdbc)
	                rowVal[5] = s2b(typeDesc.typeName); // TYPE_NAME (native)
	                rowVal[6] = s2b(Integer.toString(typeDesc.columnSize));
	                rowVal[7] = s2b(Integer.toString(typeDesc.bufferLength));
	                rowVal[8] = s2b(Integer.toString(typeDesc.decimalDigits));
	                rowVal[9] = s2b(Integer.toString(typeDesc.numPrecRadix));
	                rowVal[10] = s2b(Integer.toString(typeDesc.nullability));
	
	                //
	                // Doesn't always have this field, depending on version
	                //
	                //
	                // REMARK column
	                //
	                try {
	                    if (this.conn.versionMeetsMinimum(4, 1, 0)) {
	                        rowVal[11] = results.getBytes("Comment");
	                    } else {
	                        rowVal[11] = results.getBytes("Extra");
	                    }
	                } catch (Exception E) {
	                    rowVal[11] = new byte[0];
	                }
	
	                // COLUMN_DEF
	                rowVal[12] = results.getBytes("Default");
	
	                rowVal[13] = new byte[] { (byte) '0' }; // SQL_DATA_TYPE
	                rowVal[14] = new byte[] { (byte) '0' }; // SQL_DATE_TIME_SUB
	                rowVal[15] = rowVal[6]; // CHAR_OCTET_LENGTH
	
	                // ORDINAL_POSITION
	                if (!fixUpOrdinalsRequired) {
	                    rowVal[16] = Integer.toString(ordPos++).getBytes();
	                } else {
	                    String origColName = results.getString("Field");
	                    Integer realOrdinal = (Integer) ordinalFixUpMap.get(origColName);
	
	                    if (realOrdinal != null) {
	                        rowVal[16] = realOrdinal.toString().getBytes();
	                    } else {
	                        throw new SQLException("Can not find column in full column list to determine true ordinal position.",
	                            SQLError.SQL_STATE_GENERAL_ERROR);
	                    }
	                }
	
	                rowVal[17] = s2b(typeDesc.isNullable);
	
	                tuples.add(rowVal);
	            }
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
	
	    java.sql.ResultSet results = buildResultSet(fields, tuples);
	
	    return results;
	}

	/**
	 * Returns the DELETE and UPDATE foreign key actions from the given 'SHOW
	 * TABLE STATUS' string, with the DELETE action being the first item in
	 * the array, and the UPDATE action being the second.
	 *
	 * @param commentString the comment from 'SHOW TABLE STATUS'
	 *
	 * @return int[] [0] = delete action, [1] = update action
	 */
	private int[] getForeignKeyActions(String commentString) {
	    int[] actions = new int[] {
	            java.sql.DatabaseMetaData.importedKeyNoAction,
	            java.sql.DatabaseMetaData.importedKeyNoAction
	        };
	
	    int lastParenIndex = commentString.lastIndexOf(")");
	
	    if (lastParenIndex != (commentString.length() - 1)) {
	        String cascadeOptions = commentString.substring(lastParenIndex + 1)
	                                             .trim().toUpperCase(Locale.ENGLISH);
	
	        actions[0] = getCascadeDeleteOption(cascadeOptions);
	        actions[1] = getCascadeUpdateOption(cascadeOptions);
	    }
	
	    return actions;
	}

	/**
	 * Get a description of a table's indices and statistics. They are ordered
	 * by NON_UNIQUE, TYPE, INDEX_NAME, and ORDINAL_POSITION.
	 *
	 * <P>
	 * Each index column description has the following columns:
	 *
	 * <OL>
	 * <li>
	 * <B>TABLE_CAT</B> String => table catalog (may be null)
	 * </li>
	 * <li>
	 * <B>TABLE_SCHEM</B> String => table schema (may be null)
	 * </li>
	 * <li>
	 * <B>TABLE_NAME</B> String => table name
	 * </li>
	 * <li>
	 * <B>NON_UNIQUE</B> boolean => Can index values be non-unique? false when
	 * TYPE is tableIndexStatistic
	 * </li>
	 * <li>
	 * <B>INDEX_QUALIFIER</B> String => index catalog (may be null); null when
	 * TYPE is tableIndexStatistic
	 * </li>
	 * <li>
	 * <B>INDEX_NAME</B> String => index name; null when TYPE is
	 * tableIndexStatistic
	 * </li>
	 * <li>
	 * <B>TYPE</B> short => index type:
	 *
	 * <UL>
	 * <li>
	 * tableIndexStatistic - this identifies table statistics that are returned
	 * in conjuction with a table's index descriptions
	 * </li>
	 * <li>
	 * tableIndexClustered - this is a clustered index
	 * </li>
	 * <li>
	 * tableIndexHashed - this is a hashed index
	 * </li>
	 * <li>
	 * tableIndexOther - this is some other style of index
	 * </li>
	 * </ul>
	 *
	 * </li>
	 * <li>
	 * <B>ORDINAL_POSITION</B> short => column sequence number within index;
	 * zero when TYPE is tableIndexStatistic
	 * </li>
	 * <li>
	 * <B>COLUMN_NAME</B> String => column name; null when TYPE is
	 * tableIndexStatistic
	 * </li>
	 * <li>
	 * <B>ASC_OR_DESC</B> String => column sort sequence, "A" => ascending, "D"
	 * => descending, may be null if sort sequence is not supported; null when
	 * TYPE is tableIndexStatistic
	 * </li>
	 * <li>
	 * <B>CARDINALITY</B> int => When TYPE is tableIndexStatisic then this is
	 * the number of rows in the table; otherwise it is the number of unique
	 * values in the index.
	 * </li>
	 * <li>
	 * <B>PAGES</B> int => When TYPE is  tableIndexStatisic then this is the
	 * number of pages used for the table, otherwise it is the number of pages
	 * used for the current index.
	 * </li>
	 * <li>
	 * <B>FILTER_CONDITION</B> String => Filter condition, if any. (may be
	 * null)
	 * </li>
	 * </ol>
	 * </p>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog
	 * @param schema a schema name pattern; "" retrieves those without a schema
	 * @param table a table name
	 * @param unique when true, return only indices for unique values; when
	 *        false, return indices regardless of whether unique or not
	 * @param approximate when true, result is allowed to reflect approximate
	 *        or out of data values; when false, results are requested to be
	 *        accurate
	 *
	 * @return ResultSet each row is an index column description
	 *
	 * @throws SQLException DOCUMENT ME!
	 */
	public java.sql.ResultSet getIndexInfo(String catalog, String schema,
	    String table, boolean unique, boolean approximate)
	    throws SQLException {
	    /*
	     * MySQL stores index information in the following fields:
	     *
	     * Table
	     * Non_unique
	     * Key_name
	     * Seq_in_index
	     * Column_name
	     * Collation
	     * Cardinality
	     * Sub_part
	     */
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
	
	    Statement stmt = null;
	    ResultSet results = null;
	
	    try {
	        stmt = this.conn.createStatement();
	        stmt.setEscapeProcessing(false);
	
	        StringBuffer queryBuf = new StringBuffer("SHOW INDEX FROM ");
	        queryBuf.append(this.quotedId);
	        queryBuf.append(table);
	        queryBuf.append(this.quotedId);
	        queryBuf.append(databasePart);
	
	        results = stmt.executeQuery(queryBuf.toString());
	
	        Field[] fields = new Field[13];
	        fields[0] = new Field("", "TABLE_CAT", Types.CHAR, 255);
	        fields[1] = new Field("", "TABLE_SCHEM", Types.CHAR, 0);
	        fields[2] = new Field("", "TABLE_NAME", Types.CHAR, 255);
	        fields[3] = new Field("", "NON_UNIQUE", Types.CHAR, 4);
	        fields[4] = new Field("", "INDEX_QUALIFIER", Types.CHAR, 1);
	        fields[5] = new Field("", "INDEX_NAME", Types.CHAR, 32);
	        fields[6] = new Field("", "TYPE", Types.CHAR, 32);
	        fields[7] = new Field("", "ORDINAL_POSITION", Types.SMALLINT, 5);
	        fields[8] = new Field("", "COLUMN_NAME", Types.CHAR, 32);
	        fields[9] = new Field("", "ASC_OR_DESC", Types.CHAR, 1);
	        fields[10] = new Field("", "CARDINALITY", Types.INTEGER, 10);
	        fields[11] = new Field("", "PAGES", Types.INTEGER, 10);
	        fields[12] = new Field("", "FILTER_CONDITION", Types.CHAR, 32);
	
	        ArrayList rows = new ArrayList();
	
	        while (results.next()) {
                byte[][] row = new byte[14][];
                row[0] = ((catalog == null) ? new byte[0] : s2b(catalog));;
                row[1] = null;
                row[2] = results.getBytes("Table");
                
                boolean indexIsUnique = results.getInt("Non_unique") == 0;
                
                row[3] = (indexIsUnique ? s2b("true") : s2b("false"));
                row[4] = new byte[0];
                row[5] = results.getBytes("Key_name");
                row[6] = Integer.toString(java.sql.DatabaseMetaData.tableIndexOther)
                                .getBytes();
                row[7] = results.getBytes("Seq_in_index");
                row[8] = results.getBytes("Column_name");
                row[9] = results.getBytes("Collation");
                row[10] = results.getBytes("Cardinality");
                row[11] = s2b("0");
                row[12] = null;
                
                if (unique) {
                	if (indexIsUnique) {
                		rows.add(row);
                	}
                } else {
                	// All rows match
                	rows.add(row);
                }
            }

	        java.sql.ResultSet indexInfo = buildResultSet(fields, rows);
	
	        return indexInfo;
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
	 * Get a description of a table's primary key columns.  They are ordered by
	 * COLUMN_NAME.
	 *
	 * <P>
	 * Each column description has the following columns:
	 *
	 * <OL>
	 * <li>
	 * <B>TABLE_CAT</B> String => table catalog (may be null)
	 * </li>
	 * <li>
	 * <B>TABLE_SCHEM</B> String => table schema (may be null)
	 * </li>
	 * <li>
	 * <B>TABLE_NAME</B> String => table name
	 * </li>
	 * <li>
	 * <B>COLUMN_NAME</B> String => column name
	 * </li>
	 * <li>
	 * <B>KEY_SEQ</B> short => sequence number within primary key
	 * </li>
	 * <li>
	 * <B>PK_NAME</B> String => primary key name (may be null)
	 * </li>
	 * </ol>
	 * </p>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog
	 * @param schema a schema name pattern; "" retrieves those without a schema
	 * @param table a table name
	 *
	 * @return ResultSet each row is a primary key column description
	 *
	 * @throws SQLException DOCUMENT ME!
	 */
	public java.sql.ResultSet getPrimaryKeys(String catalog, String schema,
	    String table) throws SQLException {
	    Field[] fields = new Field[6];
	    fields[0] = new Field("", "TABLE_CAT", Types.CHAR, 255);
	    fields[1] = new Field("", "TABLE_SCHEM", Types.CHAR, 0);
	    fields[2] = new Field("", "TABLE_NAME", Types.CHAR, 255);
	    fields[3] = new Field("", "COLUMN_NAME", Types.CHAR, 32);
	    fields[4] = new Field("", "KEY_SEQ", Types.SMALLINT, 5);
	    fields[5] = new Field("", "PK_NAME", Types.CHAR, 32);
	
	    String dbSub = "";
	
	    if (catalog != null) {
	        if (!catalog.equals("")) {
	            dbSub = " FROM " + this.quotedId + catalog + this.quotedId;
	        }
	    } else {
	        dbSub = " FROM " + this.quotedId + this.database + this.quotedId;
	    }
	
	    if (table == null) {
	        throw new SQLException("Table not specified.",
	            SQLError.SQL_STATE_ILLEGAL_ARGUMENT);
	    }
	
	    Statement stmt = null;
	    ResultSet rs = null;
	
	    try {
	        stmt = this.conn.createStatement();
	        stmt.setEscapeProcessing(false);
	
	        StringBuffer queryBuf = new StringBuffer("SHOW KEYS FROM ");
	        queryBuf.append(this.quotedId);
	        queryBuf.append(table);
	        queryBuf.append(this.quotedId);
	        queryBuf.append(dbSub);
	
	        rs = stmt.executeQuery(queryBuf.toString());
	
	        ArrayList tuples = new ArrayList();
	        TreeMap sortMap = new TreeMap();
	
	        while (rs.next()) {
	            String keyType = rs.getString("Key_name");
	
	            if (keyType != null) {
	                if (keyType.equalsIgnoreCase("PRIMARY") ||
	                        keyType.equalsIgnoreCase("PRI")) {
	                    byte[][] tuple = new byte[6][];
	                    tuple[0] = ((catalog == null) ? new byte[0] : s2b(catalog));
	                    tuple[1] = null;
	                    tuple[2] = s2b(table);
	
	                    String columnName = rs.getString("Column_name");
	                    tuple[3] = s2b(columnName);
	                    tuple[4] = s2b(rs.getString("Seq_in_index"));
	                    tuple[5] = s2b(keyType);
	                    sortMap.put(columnName, tuple);
	                }
	            }
	        }
	
	        // Now pull out in column name sorted order
	        Iterator sortedIterator = sortMap.values().iterator();
	
	        while (sortedIterator.hasNext()) {
	            tuples.add(sortedIterator.next());
	        }
	
	        return buildResultSet(fields, tuples);
	    } finally {
	        if (rs != null) {
	            try {
	                rs.close();
	            } catch (Exception ex) {
	                ;
	            }
	
	            rs = null;
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
	 * Get a description of a catalog's stored procedure parameters and result
	 * columns.
	 *
	 * <P>
	 * Only descriptions matching the schema, procedure and parameter name
	 * criteria are returned.  They are ordered by PROCEDURE_SCHEM and
	 * PROCEDURE_NAME. Within this, the return value, if any, is first. Next
	 * are the parameter descriptions in call order. The column descriptions
	 * follow in column number order.
	 * </p>
	 *
	 * <P>
	 * Each row in the ResultSet is a parameter desription or column
	 * description with the following fields:
	 *
	 * <OL>
	 * <li>
	 * <B>PROCEDURE_CAT</B> String => procedure catalog (may be null)
	 * </li>
	 * <li>
	 * <B>PROCEDURE_SCHEM</B> String => procedure schema (may be null)
	 * </li>
	 * <li>
	 * <B>PROCEDURE_NAME</B> String => procedure name
	 * </li>
	 * <li>
	 * <B>COLUMN_NAME</B> String => column/parameter name
	 * </li>
	 * <li>
	 * <B>COLUMN_TYPE</B> Short => kind of column/parameter:
	 *
	 * <UL>
	 * <li>
	 * procedureColumnUnknown - nobody knows
	 * </li>
	 * <li>
	 * procedureColumnIn - IN parameter
	 * </li>
	 * <li>
	 * procedureColumnInOut - INOUT parameter
	 * </li>
	 * <li>
	 * procedureColumnOut - OUT parameter
	 * </li>
	 * <li>
	 * procedureColumnReturn - procedure return value
	 * </li>
	 * <li>
	 * procedureColumnResult - result column in ResultSet
	 * </li>
	 * </ul>
	 *
	 * </li>
	 * <li>
	 * <B>DATA_TYPE</B> short => SQL type from java.sql.Types
	 * </li>
	 * <li>
	 * <B>TYPE_NAME</B> String => SQL type name
	 * </li>
	 * <li>
	 * <B>PRECISION</B> int => precision
	 * </li>
	 * <li>
	 * <B>LENGTH</B> int => length in bytes of data
	 * </li>
	 * <li>
	 * <B>SCALE</B> short => scale
	 * </li>
	 * <li>
	 * <B>RADIX</B> short => radix
	 * </li>
	 * <li>
	 * <B>NULLABLE</B> short => can it contain NULL?
	 *
	 * <UL>
	 * <li>
	 * procedureNoNulls - does not allow NULL values
	 * </li>
	 * <li>
	 * procedureNullable - allows NULL values
	 * </li>
	 * <li>
	 * procedureNullableUnknown - nullability unknown
	 * </li>
	 * </ul>
	 *
	 * </li>
	 * <li>
	 * <B>REMARKS</B> String => comment describing parameter/column
	 * </li>
	 * </ol>
	 * </p>
	 *
	 * <P>
	 * <B>Note:</B> Some databases may not return the column descriptions for a
	 * procedure. Additional columns beyond REMARKS can be defined by the
	 * database.
	 * </p>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog
	 * @param schemaPattern a schema name pattern; "" retrieves those without a
	 *        schema
	 * @param procedureNamePattern a procedure name pattern
	 * @param columnNamePattern a column name pattern
	 *
	 * @return ResultSet each row is a stored procedure parameter or column
	 *         description
	 *
	 * @throws SQLException if a database access error occurs
	 *
	 * @see #getSearchStringEscape
	 */
	public java.sql.ResultSet getProcedureColumns(String catalog,
	    String schemaPattern, String procedureNamePattern,
	    String columnNamePattern) throws SQLException {
	    if (procedureNamePattern == null) {
	    }
	
	    if (columnNamePattern == null) {
	    }
	
	    Field[] fields = new Field[13];
	
	    fields[0] = new Field("", "PROCEDURE_CAT", Types.CHAR, 0);
	    fields[1] = new Field("", "PROCEDURE_SCHEM", Types.CHAR, 0);
	    fields[2] = new Field("", "PROCEDURE_NAME", Types.CHAR, 0);
	    fields[3] = new Field("", "COLUMN_NAME", Types.CHAR, 0);
	    fields[4] = new Field("", "COLUMN_TYPE", Types.CHAR, 0);
	    fields[5] = new Field("", "DATA_TYPE", Types.SMALLINT, 0);
	    fields[6] = new Field("", "TYPE_NAME", Types.CHAR, 0);
	    fields[7] = new Field("", "PRECISION", Types.INTEGER, 0);
	    fields[8] = new Field("", "LENGTH", Types.INTEGER, 0);
	    fields[9] = new Field("", "SCALE", Types.SMALLINT, 0);
	    fields[10] = new Field("", "RADIX", Types.SMALLINT, 0);
	    fields[11] = new Field("", "NULLABLE", Types.SMALLINT, 0);
	    fields[12] = new Field("", "REMARKS", Types.CHAR, 0);
	
	    List proceduresToExtractList = new ArrayList();
	
	    if (this.supportsStoredProcedures) {
	        if ((procedureNamePattern.indexOf("%") == -1) &&
	                (procedureNamePattern.indexOf("?") == -1)) {
	            proceduresToExtractList.add(procedureNamePattern);
	        } else {
	            PreparedStatement procedureNameStmt = null;
	            ResultSet procedureNameRs = null;
	
	            try {
	                procedureNameRs = getProcedures(catalog, schemaPattern,
	                        procedureNamePattern);
	
	                // Required to be sorted in name-order by JDBC spec,
	                // in 'normal' case getProcedures takes care of this for us,
	                // but if system tables are inaccessible, we need to sort...
	                // so just do this to be safe...
	                Collections.sort(proceduresToExtractList);
	
	                while (procedureNameRs.next()) {
	                    proceduresToExtractList.add(procedureNameRs.getString(3));
	                }
	            } finally {
	                SQLException rethrowSqlEx = null;
	
	                if (procedureNameRs != null) {
	                    try {
	                        procedureNameRs.close();
	                    } catch (SQLException sqlEx) {
	                        rethrowSqlEx = sqlEx;
	                    }
	                }
	
	                if (procedureNameStmt != null) {
	                    try {
	                        procedureNameStmt.close();
	                    } catch (SQLException sqlEx) {
	                        rethrowSqlEx = sqlEx;
	                    }
	                }
	
	                if (rethrowSqlEx != null) {
	                    throw rethrowSqlEx;
	                }
	            }
	        }
	    }
	
	    ArrayList resultRows = new ArrayList();
	
	    for (Iterator iter = proceduresToExtractList.iterator();
	            iter.hasNext();) {
	        String procName = (String) iter.next();
	
	        getCallStmtParameterTypes(procName, columnNamePattern, resultRows);
	    }
	
	    return buildResultSet(fields, resultRows);
	}

	/**
	 * Get a description of stored procedures available in a catalog.
	 *
	 * <P>
	 * Only procedure descriptions matching the schema and procedure name
	 * criteria are returned.  They are ordered by PROCEDURE_SCHEM, and
	 * PROCEDURE_NAME.
	 * </p>
	 *
	 * <P>
	 * Each procedure description has the the following columns:
	 *
	 * <OL>
	 * <li>
	 * <B>PROCEDURE_CAT</B> String => procedure catalog (may be null)
	 * </li>
	 * <li>
	 * <B>PROCEDURE_SCHEM</B> String => procedure schema (may be null)
	 * </li>
	 * <li>
	 * <B>PROCEDURE_NAME</B> String => procedure name
	 * </li>
	 * <li>
	 * reserved for future use
	 * </li>
	 * <li>
	 * reserved for future use
	 * </li>
	 * <li>
	 * reserved for future use
	 * </li>
	 * <li>
	 * <B>REMARKS</B> String => explanatory comment on the procedure
	 * </li>
	 * <li>
	 * <B>PROCEDURE_TYPE</B> short => kind of procedure:
	 *
	 * <UL>
	 * <li>
	 * procedureResultUnknown - May return a result
	 * </li>
	 * <li>
	 * procedureNoResult - Does not return a result
	 * </li>
	 * <li>
	 * procedureReturnsResult - Returns a result
	 * </li>
	 * </ul>
	 *
	 * </li>
	 * </ol>
	 * </p>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog
	 * @param schemaPattern a schema name pattern; "" retrieves those without a
	 *        schema
	 * @param procedureNamePattern a procedure name pattern
	 *
	 * @return ResultSet each row is a procedure description
	 *
	 * @throws SQLException if a database access error occurs
	 *
	 * @see #getSearchStringEscape
	 */
	public java.sql.ResultSet getProcedures(String catalog,
	    String schemaPattern, String procedureNamePattern)
	    throws SQLException {
	    if ((procedureNamePattern == null) ||
	            (procedureNamePattern.length() == 0)) {
	        throw new SQLException("Procedure name pattern can not be NULL or empty.",
	            SQLError.SQL_STATE_ILLEGAL_ARGUMENT);
	    }
	
	    Field[] fields = new Field[8];
	    fields[0] = new Field("", "PROCEDURE_CAT", Types.CHAR, 0);
	    fields[1] = new Field("", "PROCEDURE_SCHEM", Types.CHAR, 0);
	    fields[2] = new Field("", "PROCEDURE_NAME", Types.CHAR, 0);
	    fields[3] = new Field("", "reserved1", Types.CHAR, 0);
	    fields[4] = new Field("", "reserved2", Types.CHAR, 0);
	    fields[5] = new Field("", "reserved3", Types.CHAR, 0);
	    fields[6] = new Field("", "REMARKS", Types.CHAR, 0);
	    fields[7] = new Field("", "PROCEDURE_TYPE", Types.SMALLINT, 0);
	
	    ArrayList procedureRows = new ArrayList();
	
	    if (this.supportsStoredProcedures) {
	        PreparedStatement proceduresStmt = null;
	        ResultSet proceduresRs = null;
	
	        try {
	            //proceduresStmt = this.conn.clientPrepareStatement(
	            //        "SHOW PROCEDURE STATUS LIKE ?");
	            proceduresStmt = this.conn.clientPrepareStatement(
	                    "SELECT name FROM mysql.proc WHERE name like ? ORDER BY name");
	
	            int nameIndex = 1;
	
	            if (proceduresStmt.getMaxRows() != 0) {
	                proceduresStmt.setMaxRows(0);
	            }
	
	            proceduresStmt.setString(1, procedureNamePattern);
	
	            try {
	                proceduresRs = proceduresStmt.executeQuery();
	            } catch (SQLException sqlEx) {
	                //
	                // Okay, system tables aren't accessible, so use 'SHOW ....'....
	                //
	                proceduresStmt.close();
	
	                proceduresStmt = this.conn.clientPrepareStatement(
	                        "SHOW PROCEDURE STATUS LIKE ?");
	
	                if (proceduresStmt.getMaxRows() != 0) {
	                    proceduresStmt.setMaxRows(0);
	                }
	
	                proceduresStmt.setString(1, procedureNamePattern);
	
	                proceduresRs = proceduresStmt.executeQuery();
	
	                if (this.conn.versionMeetsMinimum(5, 0, 1)) {
	                    nameIndex = 2;
	                } else {
	                    nameIndex = 1;
	                }
	            }
	
	            while (proceduresRs.next()) {
	                byte[][] rowData = new byte[8][];
	                rowData[0] = null;
	                rowData[1] = null;
	                rowData[2] = s2b(proceduresRs.getString(nameIndex));
	                rowData[3] = null;
	                rowData[4] = null;
	                rowData[5] = null;
	                rowData[6] = null;
	                rowData[7] = s2b(Integer.toString(DatabaseMetaData.procedureResultUnknown));
	
	                procedureRows.add(rowData);
	            }
	        } finally {
	            SQLException rethrowSqlEx = null;
	
	            if (proceduresRs != null) {
	                try {
	                    proceduresRs.close();
	                } catch (SQLException sqlEx) {
	                    rethrowSqlEx = sqlEx;
	                }
	            }
	
	            if (proceduresStmt != null) {
	                try {
	                    proceduresStmt.close();
	                } catch (SQLException sqlEx) {
	                    rethrowSqlEx = sqlEx;
	                }
	            }
	
	            if (rethrowSqlEx != null) {
	                throw rethrowSqlEx;
	            }
	        }
	    }
	
	    return buildResultSet(fields, procedureRows);
	}

	/**
	 * Get the schema names available in this database.  The results are
	 * ordered by schema name.
	 *
	 * <P>
	 * The schema column is:
	 *
	 * <OL>
	 * <li>
	 * <B>TABLE_SCHEM</B> String => schema name
	 * </li>
	 * </ol>
	 * </p>
	 *
	 * @return ResultSet each row has a single String column that is a schema
	 *         name
	 *
	 * @throws SQLException DOCUMENT ME!
	 */
	public java.sql.ResultSet getSchemas() throws SQLException {
	    Field[] fields = new Field[1];
	    fields[0] = new Field("", "TABLE_SCHEM", java.sql.Types.CHAR, 0);
	
	    ArrayList tuples = new ArrayList();
	    java.sql.ResultSet results = buildResultSet(fields, tuples);
	
	    return results;
	}

	/**
	 * Get a description of the access rights for each table available in a
	 * catalog.
	 *
	 * <P>
	 * Only privileges matching the schema and table name criteria are
	 * returned.  They are ordered by TABLE_SCHEM, TABLE_NAME, and PRIVILEGE.
	 * </p>
	 *
	 * <P>
	 * Each privilige description has the following columns:
	 *
	 * <OL>
	 * <li>
	 * <B>TABLE_CAT</B> String => table catalog (may be null)
	 * </li>
	 * <li>
	 * <B>TABLE_SCHEM</B> String => table schema (may be null)
	 * </li>
	 * <li>
	 * <B>TABLE_NAME</B> String => table name
	 * </li>
	 * <li>
	 * <B>COLUMN_NAME</B> String => column name
	 * </li>
	 * <li>
	 * <B>GRANTOR</B> => grantor of access (may be null)
	 * </li>
	 * <li>
	 * <B>GRANTEE</B> String => grantee of access
	 * </li>
	 * <li>
	 * <B>PRIVILEGE</B> String => name of access (SELECT, INSERT, UPDATE,
	 * REFRENCES, ...)
	 * </li>
	 * <li>
	 * <B>IS_GRANTABLE</B> String => "YES" if grantee is permitted to grant to
	 * others; "NO" if not; null if unknown
	 * </li>
	 * </ol>
	 * </p>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog
	 * @param schemaPattern a schema name pattern; "" retrieves those without a
	 *        schema
	 * @param tableNamePattern a table name pattern
	 *
	 * @return ResultSet each row is a table privilege description
	 *
	 * @throws SQLException if a database access error occurs
	 *
	 * @see #getSearchStringEscape
	 */
	public java.sql.ResultSet getTablePrivileges(String catalog,
	    String schemaPattern, String tableNamePattern)
	    throws SQLException {
	    Field[] fields = new Field[7];
	    fields[0] = new Field("", "TABLE_CAT", Types.CHAR, 64);
	    fields[1] = new Field("", "TABLE_SCHEM", Types.CHAR, 1);
	    fields[2] = new Field("", "TABLE_NAME", Types.CHAR, 64);
	    fields[3] = new Field("", "GRANTOR", Types.CHAR, 77);
	    fields[4] = new Field("", "GRANTEE", Types.CHAR, 77);
	    fields[5] = new Field("", "PRIVILEGE", Types.CHAR, 64);
	    fields[6] = new Field("", "IS_GRANTABLE", Types.CHAR, 3);
	
	    StringBuffer grantQuery = new StringBuffer(
	            "SELECT host,db,table_name,grantor,user,table_priv from mysql.tables_priv ");
	    grantQuery.append(" WHERE ");
	
	    if ((catalog != null) && (catalog.length() != 0)) {
	        grantQuery.append(" db='");
	        grantQuery.append(catalog);
	        grantQuery.append("' AND ");
	    }
	
	    grantQuery.append("table_name like '");
	    grantQuery.append(tableNamePattern);
	    grantQuery.append("'");
	
	    ResultSet results = null;
	    ArrayList grantRows = new ArrayList();
	    Statement stmt = null;
	
	    try {
	        stmt = this.conn.createStatement();
	        stmt.setEscapeProcessing(false);
	
	        results = stmt.executeQuery(grantQuery.toString());
	
	        while (results.next()) {
	            String host = results.getString(1);
	            String db = results.getString(2);
	            String table = results.getString(3);
	            String grantor = results.getString(4);
	            String user = results.getString(5);
	
	            if ((user == null) || (user.length() == 0)) {
	                user = "%";
	            }
	
	            StringBuffer fullUser = new StringBuffer(user);
	
	            if ((host != null) && this.conn.getUseHostsInPrivileges()) {
	                fullUser.append("@");
	                fullUser.append(host);
	            }
	
	            String allPrivileges = results.getString(6);
	
	            if (allPrivileges != null) {
	                allPrivileges = allPrivileges.toUpperCase(Locale.ENGLISH);
	
	                StringTokenizer st = new StringTokenizer(allPrivileges, ",");
	
	                while (st.hasMoreTokens()) {
	                    String privilege = st.nextToken().trim();
	
	                    // Loop through every column in the table
	                    java.sql.ResultSet columnResults = null;
	
	                    try {
	                        columnResults = getColumns(catalog, schemaPattern,
	                                table, "%");
	
	                        while (columnResults.next()) {
	                            byte[][] tuple = new byte[8][];
	                            tuple[0] = s2b(db);
	                            tuple[1] = null;
	                            tuple[2] = s2b(table);
	
	                            if (grantor != null) {
	                                tuple[3] = s2b(grantor);
	                            } else {
	                                tuple[3] = null;
	                            }
	
	                            tuple[4] = s2b(fullUser.toString());
	                            tuple[5] = s2b(privilege);
	                            tuple[6] = null;
	                            grantRows.add(tuple);
	                        }
	                    } finally {
	                        if (columnResults != null) {
	                            try {
	                                columnResults.close();
	                            } catch (Exception ex) {
	                                ;
	                            }
	                        }
	                    }
	                }
	            }
	        }
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
	
	    return buildResultSet(fields, grantRows);
	}

	/**
	 * Get a description of tables available in a catalog.
	 *
	 * <P>
	 * Only table descriptions matching the catalog, schema, table name and
	 * type criteria are returned.  They are ordered by TABLE_TYPE,
	 * TABLE_SCHEM and TABLE_NAME.
	 * </p>
	 *
	 * <P>
	 * Each table description has the following columns:
	 *
	 * <OL>
	 * <li>
	 * <B>TABLE_CAT</B> String => table catalog (may be null)
	 * </li>
	 * <li>
	 * <B>TABLE_SCHEM</B> String => table schema (may be null)
	 * </li>
	 * <li>
	 * <B>TABLE_NAME</B> String => table name
	 * </li>
	 * <li>
	 * <B>TABLE_TYPE</B> String => table type.  Typical types are "TABLE",
	 * "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS",
	 * "SYNONYM".
	 * </li>
	 * <li>
	 * <B>REMARKS</B> String => explanatory comment on the table
	 * </li>
	 * </ol>
	 * </p>
	 *
	 * <P>
	 * <B>Note:</B> Some databases may not return information for all tables.
	 * </p>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog
	 * @param schemaPattern a schema name pattern; "" retrieves those without a
	 *        schema
	 * @param tableNamePattern a table name pattern
	 * @param types a list of table types to include; null returns all types
	 *
	 * @return ResultSet each row is a table description
	 *
	 * @throws SQLException DOCUMENT ME!
	 *
	 * @see #getSearchStringEscape
	 */
	public java.sql.ResultSet getTables(String catalog, String schemaPattern,
	    String tableNamePattern, String[] types) throws SQLException {
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
	
	    if (tableNamePattern == null) {
	        tableNamePattern = "%";
	    }
	
	    Statement stmt = null;
	    ResultSet results = null;
	
	    try {
	        stmt = this.conn.createStatement();
	        stmt.setEscapeProcessing(false);
	
	        if (!this.conn.versionMeetsMinimum(5, 0, 2)) {
	        	results = stmt.executeQuery("SHOW TABLES " + databasePart +
	                " LIKE '" + tableNamePattern + "'");
	        } else {
	        	results = stmt.executeQuery("SHOW FULL TABLES " + databasePart +
	                    " LIKE '" + tableNamePattern + "'");
	        }
	
	        Field[] fields = new Field[5];
	        fields[0] = new Field("", "TABLE_CAT", java.sql.Types.VARCHAR,
	                (catalog == null) ? 0 : catalog.length());
	        fields[1] = new Field("", "TABLE_SCHEM", java.sql.Types.VARCHAR, 0);
	        fields[2] = new Field("", "TABLE_NAME", java.sql.Types.VARCHAR, 255);
	        fields[3] = new Field("", "TABLE_TYPE", java.sql.Types.VARCHAR, 5);
	        fields[4] = new Field("", "REMARKS", java.sql.Types.VARCHAR, 0);
	
	        ArrayList tuples = new ArrayList();
	       
	        boolean shouldReportTables = false;
	        boolean shouldReportViews = false;
	
	        if (types == null) {
	            shouldReportTables = true;
	            shouldReportViews = true;
	        } else {
	            for (int i = 0; i < types.length; i++) {
	                if ("TABLE".equalsIgnoreCase(types[i])) {
	                    shouldReportTables = true;
	                }
	
	                if ("VIEW".equalsIgnoreCase(types[i])) {
	                    shouldReportViews = true;
	                }
	            }
	        }
	
	        int typeColumnIndex = 0;
	        boolean hasTableTypes = false;
	        
	        if (shouldReportViews) {
	        	try {
	        		// Both column names have been in use in the source tree
	        		// so far....
	        		typeColumnIndex = results.findColumn("table_type");
	        		hasTableTypes = true;
	        	} catch (SQLException sqlEx) {
	        		
	                // We should probably check SQLState here, but that
	                // can change depending on the server version and
	                // user properties, however, we'll get a 'true' 
	                // SQLException when we actually try to find the 
	                // 'Type' column
	                // 
	        		try {
	        			typeColumnIndex = results.findColumn("Type");
	            		hasTableTypes = true;
	        		} catch (SQLException sqlEx2) {
	        			hasTableTypes = false;
	        		}
	        	}
	        }
	
	        TreeMap tablesOrderedByName = null;
	        TreeMap viewsOrderedByName = null;
	        
	        while (results.next()) {
	        	byte[][] row = new byte[5][];
	            row[0] = (catalog == null) ? new byte[0] : s2b(catalog);
	            row[1] = null;
	            row[2] = results.getBytes(1);
	            row[4] = new byte[0];
	
	            if (hasTableTypes) {
	                String tableType = results.getString(typeColumnIndex);
	
	                if (("table".equalsIgnoreCase(tableType) || 
	                		"base table".equalsIgnoreCase(tableType)) &&
	                        shouldReportTables) {
	                    row[3] = TABLE_AS_BYTES;
	                    
	                    if (tablesOrderedByName == null) {
	                    	tablesOrderedByName = new TreeMap();
	                    }
	                    
	                    tablesOrderedByName.put(results.getString(1), row);
	                } else if ("view".equalsIgnoreCase(tableType) &&
	                        shouldReportViews) {
	                    row[3] = VIEW_AS_BYTES;
	                    
	                    if (viewsOrderedByName == null) {
	                    	viewsOrderedByName = new TreeMap();
	                    }
	                    
	                    viewsOrderedByName.put(results.getString(1), row);
	                } else {
	                    // punt?
	                    row[3] = TABLE_AS_BYTES;
	                    
	                    if (tablesOrderedByName == null) {
	                    	tablesOrderedByName = new TreeMap();
	                    }
	                    
	                    tablesOrderedByName.put(results.getString(1), row);
	                }
	            } else {
	                // Pre-MySQL-5.0.1, tables only
	                row[3] = TABLE_AS_BYTES;
	                
	                if (tablesOrderedByName == null) {
	                	tablesOrderedByName = new TreeMap();
	                }
	                
	                tablesOrderedByName.put(results.getString(1), row);
	            }
	        }
	
	        //They are ordered by TABLE_TYPE,
	        // * TABLE_SCHEM and TABLE_NAME.
	        
	        if (tablesOrderedByName != null) {
	        	Iterator tablesIter = tablesOrderedByName.values().iterator();
	
	        	while (tablesIter.hasNext()) {
	        		tuples.add(tablesIter.next());
	        	}
	        }
	
	        if (viewsOrderedByName != null) {
	        	Iterator viewsIter = viewsOrderedByName.values().iterator();
	
	        	while (viewsIter.hasNext()) {
	        		tuples.add(viewsIter.next());
	        	}
	        }
	
	        java.sql.ResultSet tables = buildResultSet(fields, tuples);
	
	        return tables;
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
	 * Get a description of a table's columns that are automatically updated
	 * when any value in a row is updated.  They are unordered.
	 *
	 * <P>
	 * Each column description has the following columns:
	 *
	 * <OL>
	 * <li>
	 * <B>SCOPE</B> short => is not used
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
	 * <B>BUFFER_LENGTH</B> int => length of column value in bytes
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
	 * versionColumnUnknown - may or may not be pseudo column
	 * </li>
	 * <li>
	 * versionColumnNotPseudo - is NOT a pseudo column
	 * </li>
	 * <li>
	 * versionColumnPseudo - is a pseudo column
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
	 *
	 * @return ResultSet each row is a column description
	 *
	 * @throws SQLException DOCUMENT ME!
	 */
	public java.sql.ResultSet getVersionColumns(String catalog, String schema,
	    String table) throws SQLException {
	    Field[] fields = new Field[8];
	    fields[0] = new Field("", "SCOPE", Types.SMALLINT, 5);
	    fields[1] = new Field("", "COLUMN_NAME", Types.CHAR, 32);
	    fields[2] = new Field("", "DATA_TYPE", Types.SMALLINT, 5);
	    fields[3] = new Field("", "TYPE_NAME", Types.CHAR, 16);
	    fields[4] = new Field("", "COLUMN_SIZE", Types.CHAR, 16);
	    fields[5] = new Field("", "BUFFER_LENGTH", Types.CHAR, 16);
	    fields[6] = new Field("", "DECIMAL_DIGITS", Types.CHAR, 16);
	    fields[7] = new Field("", "PSEUDO_COLUMN", Types.SMALLINT, 5);
	
	    return buildResultSet(fields, new ArrayList());
	
	    // do TIMESTAMP columns count?
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

	/**
	 * Adds to the tuples list the exported keys of exportingTable based on the
	 * keysComment from the 'show table status' sql command. KeysComment is
	 * that part of the comment field that follows the "InnoDB free ...;"
	 * prefix.
	 *
	 * @param catalog the database to use
	 * @param exportingTable the table keys are being exported from
	 * @param keysComment the comment from 'show table status'
	 * @param tuples the rows to add results to
	 * @param fkTableName the foreign key table name
	 *
	 * @throws SQLException if a database access error occurs
	 */
	private void getExportKeyResults(String catalog, String exportingTable,
	    String keysComment, List tuples, String fkTableName)
	    throws SQLException {
	    getResultsImpl(catalog, exportingTable, keysComment, tuples,
	        fkTableName, true);
	}

	/**
	 * Populates the tuples list with the imported keys of importingTable based
	 * on the keysComment from the 'show table status' sql command.
	 * KeysComment is that part of the comment field that follows the "InnoDB
	 * free ...;" prefix.
	 *
	 * @param catalog the database to use
	 * @param importingTable the table keys are being imported to
	 * @param keysComment the comment from 'show table status'
	 * @param tuples the rows to add results to
	 *
	 * @throws SQLException if a database access error occurs
	 */
	private void getImportKeyResults(String catalog, String importingTable,
	    String keysComment, List tuples) throws SQLException {
	    getResultsImpl(catalog, importingTable, keysComment, tuples, null, false);
	}

	private void getResultsImpl(String catalog, String table,
	    String keysComment, List tuples, String fkTableName, boolean isExport)
	    throws SQLException {
	    // keys will equal something like this:
	    // (parent_service_id child_service_id) REFER ds/subservices(parent_service_id child_service_id)
	    // parse of the string into three phases:
	    // 1: parse the opening parentheses to determine how many results there will be
	    // 2: read in the schema name/table name
	    // 3: parse the closing parentheses
	    int firstLeftParenIndex = keysComment.indexOf('(');
	    String constraintName = keysComment.substring(0, firstLeftParenIndex)
	                                       .trim();
	    keysComment = keysComment.substring(firstLeftParenIndex,
	            keysComment.length());
	
	    StringTokenizer keyTokens = new StringTokenizer(keysComment.trim(),
	            "()", false);
	    String localColumnNamesString = keyTokens.nextToken();
	    StringTokenizer localColumnNames = new StringTokenizer(localColumnNamesString,
	            " ,");
	    String referCatalogTableString = keyTokens.nextToken();
	    StringTokenizer referSchemaTable = new StringTokenizer(referCatalogTableString,
	            " /");
	    String referColumnNamesString = keyTokens.nextToken();
	    StringTokenizer referColumnNames = new StringTokenizer(referColumnNamesString,
	            " ,");
	    referSchemaTable.nextToken(); //discard the REFER token
	
	    String referCatalog = referSchemaTable.nextToken();
	    String referTable = referSchemaTable.nextToken();
	
	    if (isExport && !referTable.equals(table)) {
	        return;
	    }
	
	    if (localColumnNames.countTokens() != referColumnNames.countTokens()) {
	        throw new SQLException("Error parsing foriegn keys definition",
	            SQLError.SQL_STATE_GENERAL_ERROR);
	    }
	
	    int keySeqIndex = 1;
	
	    while (localColumnNames.hasMoreTokens()) {
	        byte[][] tuple = new byte[14][];
	        String localColumnName = localColumnNames.nextToken();
	        String referColumnName = referColumnNames.nextToken();
	        tuple[FKTABLE_CAT] = ((catalog == null) ? new byte[0] : s2b(catalog));
	        tuple[FKTABLE_SCHEM] = null;
	        tuple[FKTABLE_NAME] = s2b((isExport) ? fkTableName : table);
	        tuple[FKCOLUMN_NAME] = s2b(localColumnName);
	        tuple[PKTABLE_CAT] = s2b(referCatalog);
	        tuple[PKTABLE_SCHEM] = null;
	        tuple[PKTABLE_NAME] = s2b((isExport) ? table : referTable);
	        tuple[PKCOLUMN_NAME] = s2b(referColumnName);
	        tuple[KEY_SEQ] = s2b(Integer.toString(keySeqIndex++));
	
	        int[] actions = getForeignKeyActions(keysComment);
	
	        tuple[UPDATE_RULE] = s2b(Integer.toString(actions[1]));
	        tuple[DELETE_RULE] = s2b(Integer.toString(actions[0]));
	        tuple[FK_NAME] = s2b(constraintName);
	        tuple[PK_NAME] = null; //not available from show table status
	        tuple[DEFERRABILITY] = s2b(Integer.toString(
	                    java.sql.DatabaseMetaData.importedKeyNotDeferrable));
	        tuples.add(tuple);
	    }
	}

	private String getTableNameWithCase(String table) {
	    String tableNameWithCase = (this.conn.lowerCaseTableNames()
	        ? table.toLowerCase() : table);
	
	    return tableNameWithCase;
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
	 * Parses the cascade option string and returns the DBMD constant that
	 * represents it (for deletes)
	 *
	 * @param cascadeOptions the comment from 'SHOW TABLE STATUS'
	 *
	 * @return the DBMD constant that represents the cascade option
	 */
	private int getCascadeDeleteOption(String cascadeOptions) {
	    int onDeletePos = cascadeOptions.indexOf("ON DELETE");
	
	    if (onDeletePos != -1) {
	        String deleteOptions = cascadeOptions.substring(onDeletePos,
	                cascadeOptions.length());
	
	        if (deleteOptions.startsWith("ON DELETE CASCADE")) {
	            return java.sql.DatabaseMetaData.importedKeyCascade;
	        } else if (deleteOptions.startsWith("ON DELETE SET NULL")) {
	            return java.sql.DatabaseMetaData.importedKeySetNull;
	        } else if (deleteOptions.startsWith("ON DELETE RESTRICT")) {
	            return java.sql.DatabaseMetaData.importedKeyRestrict;
	        } else if (deleteOptions.startsWith("ON DELETE NO ACTION")) {
	            return java.sql.DatabaseMetaData.importedKeyNoAction;
	        }
	    }
	
	    return java.sql.DatabaseMetaData.importedKeyNoAction;
	}

	/**
	 * Parses the cascade option string and returns the DBMD constant that
	 * represents it (for Updates)
	 *
	 * @param cascadeOptions the comment from 'SHOW TABLE STATUS'
	 *
	 * @return the DBMD constant that represents the cascade option
	 */
	private int getCascadeUpdateOption(String cascadeOptions) {
	    int onUpdatePos = cascadeOptions.indexOf("ON UPDATE");
	
	    if (onUpdatePos != -1) {
	        String updateOptions = cascadeOptions.substring(onUpdatePos,
	                cascadeOptions.length());
	
	        if (updateOptions.startsWith("ON UPDATE CASCADE")) {
	            return java.sql.DatabaseMetaData.importedKeyCascade;
	        } else if (updateOptions.startsWith("ON UPDATE SET NULL")) {
	            return java.sql.DatabaseMetaData.importedKeySetNull;
	        } else if (updateOptions.startsWith("ON UPDATE RESTRICT")) {
	            return java.sql.DatabaseMetaData.importedKeyRestrict;
	        } else if (updateOptions.startsWith("ON UPDATE NO ACTION")) {
	            return java.sql.DatabaseMetaData.importedKeyNoAction;
	        }
	    }
	
	    return java.sql.DatabaseMetaData.importedKeyNoAction;
	}

	/**
	 * Get a description of the foreign key columns in the foreign key table
	 * that reference the primary key columns of the primary key table
	 * (describe how one table imports another's key.) This should normally
	 * return a single foreign key/primary key pair (most tables only import a
	 * foreign key from a table once.)  They are ordered by FKTABLE_CAT,
	 * FKTABLE_SCHEM, FKTABLE_NAME, and KEY_SEQ.
	 *
	 * <P>
	 * Each foreign key column description has the following columns:
	 *
	 * <OL>
	 * <li>
	 * <B>PKTABLE_CAT</B> String => primary key table catalog (may be null)
	 * </li>
	 * <li>
	 * <B>PKTABLE_SCHEM</B> String => primary key table schema (may be null)
	 * </li>
	 * <li>
	 * <B>PKTABLE_NAME</B> String => primary key table name
	 * </li>
	 * <li>
	 * <B>PKCOLUMN_NAME</B> String => primary key column name
	 * </li>
	 * <li>
	 * <B>FKTABLE_CAT</B> String => foreign key table catalog (may be null)
	 * being exported (may be null)
	 * </li>
	 * <li>
	 * <B>FKTABLE_SCHEM</B> String => foreign key table schema (may be null)
	 * being exported (may be null)
	 * </li>
	 * <li>
	 * <B>FKTABLE_NAME</B> String => foreign key table name being exported
	 * </li>
	 * <li>
	 * <B>FKCOLUMN_NAME</B> String => foreign key column name being exported
	 * </li>
	 * <li>
	 * <B>KEY_SEQ</B> short => sequence number within foreign key
	 * </li>
	 * <li>
	 * <B>UPDATE_RULE</B> short => What happens to foreign key when primary is
	 * updated:
	 *
	 * <UL>
	 * <li>
	 * importedKeyCascade - change imported key to agree with primary key
	 * update
	 * </li>
	 * <li>
	 * importedKeyRestrict - do not allow update of primary key if it has been
	 * imported
	 * </li>
	 * <li>
	 * importedKeySetNull - change imported key to NULL if its primary key has
	 * been updated
	 * </li>
	 * </ul>
	 *
	 * </li>
	 * <li>
	 * <B>DELETE_RULE</B> short => What happens to the foreign key when primary
	 * is deleted.
	 *
	 * <UL>
	 * <li>
	 * importedKeyCascade - delete rows that import a deleted key
	 * </li>
	 * <li>
	 * importedKeyRestrict - do not allow delete of primary key if it has been
	 * imported
	 * </li>
	 * <li>
	 * importedKeySetNull - change imported key to NULL if its primary key has
	 * been deleted
	 * </li>
	 * </ul>
	 *
	 * </li>
	 * <li>
	 * <B>FK_NAME</B> String => foreign key identifier (may be null)
	 * </li>
	 * <li>
	 * <B>PK_NAME</B> String => primary key identifier (may be null)
	 * </li>
	 * </ol>
	 * </p>
	 *
	 * @param primaryCatalog a catalog name; "" retrieves those without a
	 *        catalog
	 * @param primarySchema a schema name pattern; "" retrieves those without a
	 *        schema
	 * @param primaryTable a table name
	 * @param foreignCatalog a catalog name; "" retrieves those without a
	 *        catalog
	 * @param foreignSchema a schema name pattern; "" retrieves those without a
	 *        schema
	 * @param foreignTable a table name
	 *
	 * @return ResultSet each row is a foreign key column description
	 *
	 * @throws SQLException if a database access error occurs
	 */
	public java.sql.ResultSet getCrossReference(String primaryCatalog,
	    String primarySchema, String primaryTable, String foreignCatalog,
	    String foreignSchema, String foreignTable) throws SQLException {
	    if (primaryTable == null) {
	        throw new SQLException("Table not specified.",
	            SQLError.SQL_STATE_ILLEGAL_ARGUMENT);
	    }
	
	    Field[] fields = new Field[14];
	    fields[0] = new Field("", "PKTABLE_CAT", Types.CHAR, 255);
	    fields[1] = new Field("", "PKTABLE_SCHEM", Types.CHAR, 0);
	    fields[2] = new Field("", "PKTABLE_NAME", Types.CHAR, 255);
	    fields[3] = new Field("", "PKCOLUMN_NAME", Types.CHAR, 32);
	    fields[4] = new Field("", "FKTABLE_CAT", Types.CHAR, 255);
	    fields[5] = new Field("", "FKTABLE_SCHEM", Types.CHAR, 0);
	    fields[6] = new Field("", "FKTABLE_NAME", Types.CHAR, 255);
	    fields[7] = new Field("", "FKCOLUMN_NAME", Types.CHAR, 32);
	    fields[8] = new Field("", "KEY_SEQ", Types.SMALLINT, 2);
	    fields[9] = new Field("", "UPDATE_RULE", Types.SMALLINT, 2);
	    fields[10] = new Field("", "DELETE_RULE", Types.SMALLINT, 2);
	    fields[11] = new Field("", "FK_NAME", Types.CHAR, 0);
	    fields[12] = new Field("", "PK_NAME", Types.CHAR, 0);
	    fields[13] = new Field("", "DEFERRABILITY", Types.INTEGER, 2);
	
	    if (this.conn.versionMeetsMinimum(3, 23, 0)) {
	        Statement stmt = null;
	        ResultSet fkresults = null;
	
	        try {
	            stmt = this.conn.createStatement();
	            stmt.setEscapeProcessing(false);
	
	            /*
	             * Get foreign key information for table
	             */
	            if (this.conn.versionMeetsMinimum(3, 23, 50)) {
	                // we can use 'SHOW CREATE TABLE'
	                String db = this.database;
	
	                if (foreignCatalog != null) {
	                    if (!foreignCatalog.equals("")) {
	                        db = foreignCatalog;
	                    }
	                }
	
	                fkresults = extractForeignKeyFromCreateTable(db, null);
	            } else {
	                String databasePart = "";
	
	                if (foreignCatalog != null) {
	                    if (!foreignCatalog.equals("")) {
	                        databasePart = " FROM " + foreignCatalog;
	                    }
	                } else {
	                    databasePart = " FROM " + this.database;
	                }
	
	                fkresults = stmt.executeQuery("show table status " +
	                        databasePart);
	            }
	
	            String foreignTableWithCase = getTableNameWithCase(foreignTable);
	            String primaryTableWithCase = getTableNameWithCase(primaryTable);
	
	            /*
	            * Parse imported foreign key information
	            */
	            ArrayList tuples = new ArrayList();
	            String dummy;
	
	            while (fkresults.next()) {
	                String tableType = fkresults.getString("Type");
	
	                if ((tableType != null) &&
	                        (tableType.equalsIgnoreCase("innodb") ||
	                        tableType.equalsIgnoreCase(SUPPORTS_FK))) {
	                    String comment = fkresults.getString("Comment").trim();
	
	                    if (comment != null) {
	                        StringTokenizer commentTokens = new StringTokenizer(comment,
	                                ";", false);
	
	                        if (commentTokens.hasMoreTokens()) {
	                            dummy = commentTokens.nextToken();
	
	                            // Skip InnoDB comment
	                        }
	
	                        while (commentTokens.hasMoreTokens()) {
	                            String keys = commentTokens.nextToken();
	
	                            // simple-columned keys: (m) REFER airline/tt(a)
	                            // multi-columned keys : (m n) REFER airline/vv(a b)
	                            int firstLeftParenIndex = keys.indexOf('(');
	                            int firstRightParenIndex = keys.indexOf(')');
	                            String referencingColumns = keys.substring(firstLeftParenIndex +
	                                    1, firstRightParenIndex);
	                            StringTokenizer referencingColumnsTokenizer = new StringTokenizer(referencingColumns,
	                                    ", ");
	                            int secondLeftParenIndex = keys.indexOf('(',
	                                    firstRightParenIndex + 1);
	                            int secondRightParenIndex = keys.indexOf(')',
	                                    firstRightParenIndex + 1);
	                            String referencedColumns = keys.substring(secondLeftParenIndex +
	                                    1, secondRightParenIndex);
	                            StringTokenizer referencedColumnsTokenizer = new StringTokenizer(referencedColumns,
	                                    ", ");
	                            int slashIndex = keys.indexOf('/');
	                            String referencedTable = keys.substring(slashIndex +
	                                    1, secondLeftParenIndex);
	                            int keySeq = 0;
	
	                            while (referencingColumnsTokenizer.hasMoreTokens()) {
	                                String referencingColumn = referencingColumnsTokenizer.nextToken();
	
	                                // one tuple for each table between parenthesis
	                                byte[][] tuple = new byte[14][];
	                                tuple[4] = ((foreignCatalog == null) ? null
	                                                                     : s2b(foreignCatalog));
	                                tuple[5] = ((foreignSchema == null) ? null
	                                                                    : s2b(foreignSchema));
	                                dummy = fkresults.getString("Name"); // FKTABLE_NAME
	
	                                if (dummy.compareTo(foreignTableWithCase) != 0) {
	                                    continue;
	                                }
	
	                                tuple[6] = s2b(dummy);
	
	                                tuple[7] = s2b(referencingColumn); // FKCOLUMN_NAME
	                                tuple[0] = ((primaryCatalog == null) ? null
	                                                                     : s2b(primaryCatalog));
	                                tuple[1] = ((primarySchema == null) ? null
	                                                                    : s2b(primarySchema));
	
	                                // Skip foreign key if it doesn't refer to the right table
	                                if (referencedTable.compareTo(
	                                            primaryTableWithCase) != 0) {
	                                    continue;
	                                }
	
	                                tuple[2] = s2b(referencedTable); // PKTABLE_NAME
	                                tuple[3] = s2b(referencedColumnsTokenizer.nextToken()); // PKCOLUMN_NAME
	                                tuple[8] = Integer.toString(keySeq)
	                                                  .getBytes(); // KEY_SEQ
	
	                                int[] actions = getForeignKeyActions(keys);
	
	                                tuple[9] = Integer.toString(actions[1])
	                                                  .getBytes();
	                                tuple[10] = Integer.toString(actions[0])
	                                                   .getBytes();
	                                tuple[11] = null; // FK_NAME
	                                tuple[12] = null; // PK_NAME
	                                tuple[13] = Integer.toString(java.sql.DatabaseMetaData.importedKeyNotDeferrable)
	                                                   .getBytes();
	                                tuples.add(tuple);
	                                keySeq++;
	                            }
	                        }
	                    }
	                }
	            }
	
	            return buildResultSet(fields, tuples);
	        } finally {
	            if (fkresults != null) {
	                try {
	                    fkresults.close();
	                } catch (Exception sqlEx) {
	                    AssertionFailedException.shouldNotHappen(sqlEx);
	                }
	
	                fkresults = null;
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
	
	    return buildResultSet(fields, new ArrayList());
	}

	/**
	 * Get a description of a foreign key columns that reference a table's
	 * primary key columns (the foreign keys exported by a table).  They are
	 * ordered by FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, and KEY_SEQ.
	 *
	 * <P>
	 * Each foreign key column description has the following columns:
	 *
	 * <OL>
	 * <li>
	 * <B>PKTABLE_CAT</B> String => primary key table catalog (may be null)
	 * </li>
	 * <li>
	 * <B>PKTABLE_SCHEM</B> String => primary key table schema (may be null)
	 * </li>
	 * <li>
	 * <B>PKTABLE_NAME</B> String => primary key table name
	 * </li>
	 * <li>
	 * <B>PKCOLUMN_NAME</B> String => primary key column name
	 * </li>
	 * <li>
	 * <B>FKTABLE_CAT</B> String => foreign key table catalog (may be null)
	 * being exported (may be null)
	 * </li>
	 * <li>
	 * <B>FKTABLE_SCHEM</B> String => foreign key table schema (may be null)
	 * being exported (may be null)
	 * </li>
	 * <li>
	 * <B>FKTABLE_NAME</B> String => foreign key table name being exported
	 * </li>
	 * <li>
	 * <B>FKCOLUMN_NAME</B> String => foreign key column name being exported
	 * </li>
	 * <li>
	 * <B>KEY_SEQ</B> short => sequence number within foreign key
	 * </li>
	 * <li>
	 * <B>UPDATE_RULE</B> short => What happens to foreign key when primary is
	 * updated:
	 *
	 * <UL>
	 * <li>
	 * importedKeyCascade - change imported key to agree with primary key
	 * update
	 * </li>
	 * <li>
	 * importedKeyRestrict - do not allow update of primary key if it has been
	 * imported
	 * </li>
	 * <li>
	 * importedKeySetNull - change imported key to NULL if its primary key has
	 * been updated
	 * </li>
	 * </ul>
	 *
	 * </li>
	 * <li>
	 * <B>DELETE_RULE</B> short => What happens to the foreign key when primary
	 * is deleted.
	 *
	 * <UL>
	 * <li>
	 * importedKeyCascade - delete rows that import a deleted key
	 * </li>
	 * <li>
	 * importedKeyRestrict - do not allow delete of primary key if it has been
	 * imported
	 * </li>
	 * <li>
	 * importedKeySetNull - change imported key to NULL if its primary key has
	 * been deleted
	 * </li>
	 * </ul>
	 *
	 * </li>
	 * <li>
	 * <B>FK_NAME</B> String => foreign key identifier (may be null)
	 * </li>
	 * <li>
	 * <B>PK_NAME</B> String => primary key identifier (may be null)
	 * </li>
	 * </ol>
	 * </p>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog
	 * @param schema a schema name pattern; "" retrieves those without a schema
	 * @param table a table name
	 *
	 * @return ResultSet each row is a foreign key column description
	 *
	 * @throws SQLException if a database access error occurs
	 *
	 * @see #getImportedKeys
	 */
	public java.sql.ResultSet getExportedKeys(String catalog, String schema,
	    String table) throws SQLException {
	    if (table == null) {
	        throw new SQLException("Table not specified.",
	            SQLError.SQL_STATE_ILLEGAL_ARGUMENT);
	    }
	
	    Field[] fields = new Field[14];
	    fields[0] = new Field("", "PKTABLE_CAT", Types.CHAR, 255);
	    fields[1] = new Field("", "PKTABLE_SCHEM", Types.CHAR, 0);
	    fields[2] = new Field("", "PKTABLE_NAME", Types.CHAR, 255);
	    fields[3] = new Field("", "PKCOLUMN_NAME", Types.CHAR, 32);
	    fields[4] = new Field("", "FKTABLE_CAT", Types.CHAR, 255);
	    fields[5] = new Field("", "FKTABLE_SCHEM", Types.CHAR, 0);
	    fields[6] = new Field("", "FKTABLE_NAME", Types.CHAR, 255);
	    fields[7] = new Field("", "FKCOLUMN_NAME", Types.CHAR, 32);
	    fields[8] = new Field("", "KEY_SEQ", Types.SMALLINT, 2);
	    fields[9] = new Field("", "UPDATE_RULE", Types.SMALLINT, 2);
	    fields[10] = new Field("", "DELETE_RULE", Types.SMALLINT, 2);
	    fields[11] = new Field("", "FK_NAME", Types.CHAR, 255);
	    fields[12] = new Field("", "PK_NAME", Types.CHAR, 0);
	    fields[13] = new Field("", "DEFERRABILITY", Types.INTEGER, 2);
	
	    if (this.conn.versionMeetsMinimum(3, 23, 0)) {
	        Statement stmt = null;
	        ResultSet fkresults = null;
	
	        try {
	            stmt = this.conn.createStatement();
	            stmt.setEscapeProcessing(false);
	
	            /*
	             * Get foreign key information for table
	             */
	            if (this.conn.versionMeetsMinimum(3, 23, 50)) {
	                // we can use 'SHOW CREATE TABLE'
	                String db = this.database;
	
	                if (catalog != null) {
	                    if (!catalog.equals("")) {
	                        db = catalog;
	                    }
	                }
	
	                fkresults = extractForeignKeyFromCreateTable(db, null);
	            } else {
	                String databasePart = "";
	
	                if (catalog != null) {
	                    if (!catalog.equals("")) {
	                        databasePart = " FROM " + catalog;
	                    }
	                } else {
	                    databasePart = " FROM " + this.database;
	                }
	
	                fkresults = stmt.executeQuery("show table status " +
	                        databasePart);
	            }
	
	            // lower-case table name might be turned on
	            String tableNameWithCase = getTableNameWithCase(table);
	
	            /*
	            * Parse imported foreign key information
	            */
	            ArrayList tuples = new ArrayList();
	
	            while (fkresults.next()) {
	                String tableType = fkresults.getString("Type");
	
	                if ((tableType != null) &&
	                        (tableType.equalsIgnoreCase("innodb") ||
	                        tableType.equalsIgnoreCase(SUPPORTS_FK))) {
	                    String comment = fkresults.getString("Comment").trim();
	
	                    if (comment != null) {
	                        StringTokenizer commentTokens = new StringTokenizer(comment,
	                                ";", false);
	
	                        if (commentTokens.hasMoreTokens()) {
	                            commentTokens.nextToken(); // Skip InnoDB comment
	
	                            while (commentTokens.hasMoreTokens()) {
	                                String keys = commentTokens.nextToken();
	                                getExportKeyResults(catalog,
	                                    tableNameWithCase, keys, tuples,
	                                    fkresults.getString("Name"));
	                            }
	                        }
	                    }
	                }
	            }
	
	            return buildResultSet(fields, tuples);
	        } finally {
	            if (fkresults != null) {
	                try {
	                    fkresults.close();
	                } catch (SQLException sqlEx) {
	                    AssertionFailedException.shouldNotHappen(sqlEx);
	                }
	
	                fkresults = null;
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
	
	    return buildResultSet(fields, new ArrayList());
	}

	/**
	 * Get a description of the primary key columns that are referenced by a
	 * table's foreign key columns (the primary keys imported by a table).
	 * They are ordered by PKTABLE_CAT, PKTABLE_SCHEM, PKTABLE_NAME, and
	 * KEY_SEQ.
	 *
	 * <P>
	 * Each primary key column description has the following columns:
	 *
	 * <OL>
	 * <li>
	 * <B>PKTABLE_CAT</B> String => primary key table catalog being imported
	 * (may be null)
	 * </li>
	 * <li>
	 * <B>PKTABLE_SCHEM</B> String => primary key table schema being imported
	 * (may be null)
	 * </li>
	 * <li>
	 * <B>PKTABLE_NAME</B> String => primary key table name being imported
	 * </li>
	 * <li>
	 * <B>PKCOLUMN_NAME</B> String => primary key column name being imported
	 * </li>
	 * <li>
	 * <B>FKTABLE_CAT</B> String => foreign key table catalog (may be null)
	 * </li>
	 * <li>
	 * <B>FKTABLE_SCHEM</B> String => foreign key table schema (may be null)
	 * </li>
	 * <li>
	 * <B>FKTABLE_NAME</B> String => foreign key table name
	 * </li>
	 * <li>
	 * <B>FKCOLUMN_NAME</B> String => foreign key column name
	 * </li>
	 * <li>
	 * <B>KEY_SEQ</B> short => sequence number within foreign key
	 * </li>
	 * <li>
	 * <B>UPDATE_RULE</B> short => What happens to foreign key when primary is
	 * updated:
	 *
	 * <UL>
	 * <li>
	 * importedKeyCascade - change imported key to agree with primary key
	 * update
	 * </li>
	 * <li>
	 * importedKeyRestrict - do not allow update of primary key if it has been
	 * imported
	 * </li>
	 * <li>
	 * importedKeySetNull - change imported key to NULL if its primary key has
	 * been updated
	 * </li>
	 * </ul>
	 *
	 * </li>
	 * <li>
	 * <B>DELETE_RULE</B> short => What happens to the foreign key when primary
	 * is deleted.
	 *
	 * <UL>
	 * <li>
	 * importedKeyCascade - delete rows that import a deleted key
	 * </li>
	 * <li>
	 * importedKeyRestrict - do not allow delete of primary key if it has been
	 * imported
	 * </li>
	 * <li>
	 * importedKeySetNull - change imported key to NULL if its primary key has
	 * been deleted
	 * </li>
	 * </ul>
	 *
	 * </li>
	 * <li>
	 * <B>FK_NAME</B> String => foreign key name (may be null)
	 * </li>
	 * <li>
	 * <B>PK_NAME</B> String => primary key name (may be null)
	 * </li>
	 * </ol>
	 * </p>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog
	 * @param schema a schema name pattern; "" retrieves those without a schema
	 * @param table a table name
	 *
	 * @return ResultSet each row is a primary key column description
	 *
	 * @throws SQLException if a database access error occurs
	 *
	 * @see #getExportedKeys
	 */
	public java.sql.ResultSet getImportedKeys(String catalog, String schema,
	    String table) throws SQLException {
	    if (table == null) {
	        throw new SQLException("Table not specified.",
	            SQLError.SQL_STATE_ILLEGAL_ARGUMENT);
	    }
	
	    Field[] fields = new Field[14];
	    fields[0] = new Field("", "PKTABLE_CAT", Types.CHAR, 255);
	    fields[1] = new Field("", "PKTABLE_SCHEM", Types.CHAR, 0);
	    fields[2] = new Field("", "PKTABLE_NAME", Types.CHAR, 255);
	    fields[3] = new Field("", "PKCOLUMN_NAME", Types.CHAR, 32);
	    fields[4] = new Field("", "FKTABLE_CAT", Types.CHAR, 255);
	    fields[5] = new Field("", "FKTABLE_SCHEM", Types.CHAR, 0);
	    fields[6] = new Field("", "FKTABLE_NAME", Types.CHAR, 255);
	    fields[7] = new Field("", "FKCOLUMN_NAME", Types.CHAR, 32);
	    fields[8] = new Field("", "KEY_SEQ", Types.SMALLINT, 2);
	    fields[9] = new Field("", "UPDATE_RULE", Types.SMALLINT, 2);
	    fields[10] = new Field("", "DELETE_RULE", Types.SMALLINT, 2);
	    fields[11] = new Field("", "FK_NAME", Types.CHAR, 255);
	    fields[12] = new Field("", "PK_NAME", Types.CHAR, 0);
	    fields[13] = new Field("", "DEFERRABILITY", Types.INTEGER, 2);
	
	    if (this.conn.versionMeetsMinimum(3, 23, 0)) {
	        Statement stmt = null;
	        ResultSet fkresults = null;
	
	        try {
	            stmt = this.conn.createStatement();
	            stmt.setEscapeProcessing(false);
	
	            /*
	             * Get foreign key information for table
	             */
	            if (this.conn.versionMeetsMinimum(3, 23, 50)) {
	                // we can use 'SHOW CREATE TABLE'
	                String db = this.database;
	
	                if (catalog != null) {
	                    if (!catalog.equals("")) {
	                        db = catalog;
	                    }
	                }
	
	                fkresults = extractForeignKeyFromCreateTable(db, table);
	            } else {
	                String databasePart = "";
	
	                if (catalog != null) {
	                    if (!catalog.equals("")) {
	                        databasePart = " FROM " + catalog;
	                    }
	                } else {
	                    databasePart = " FROM " + this.database;
	                }
	
	                fkresults = stmt.executeQuery("show table status " +
	                        databasePart + " like '" + table + "'");
	            }
	
	            /*
	            * Parse imported foreign key information
	            */
	            ArrayList tuples = new ArrayList();
	
	            while (fkresults.next()) {
	                String tableType = fkresults.getString("Type");
	
	                if ((tableType != null) &&
	                        (tableType.equalsIgnoreCase("innodb") ||
	                        tableType.equalsIgnoreCase(SUPPORTS_FK))) {
	                    String comment = fkresults.getString("Comment").trim();
	
	                    if (comment != null) {
	                        StringTokenizer commentTokens = new StringTokenizer(comment,
	                                ";", false);
	
	                        if (commentTokens.hasMoreTokens()) {
	                            commentTokens.nextToken(); // Skip InnoDB comment
	
	                            while (commentTokens.hasMoreTokens()) {
	                                String keys = commentTokens.nextToken();
	                                getImportKeyResults(catalog, table, keys,
	                                    tuples);
	                            }
	                        }
	                    }
	                }
	            }
	
	            return buildResultSet(fields, tuples);
	        } finally {
	            if (fkresults != null) {
	                try {
	                    fkresults.close();
	                } catch (SQLException sqlEx) {
	                    AssertionFailedException.shouldNotHappen(sqlEx);
	                }
	
	                fkresults = null;
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
	
	    return buildResultSet(fields, new ArrayList());
	}

	/**
	 * Extracts foreign key info for one table.
	 *
	 * @param rows the list of rows to add to
	 * @param rs the result set from 'SHOW CREATE TABLE'
	 * @param catalog the database name
	 *
	 * @return the list of rows with new rows added
	 *
	 * @throws SQLException if a database access error occurs
	 */
	private List extractForeignKeyForTable(ArrayList rows,
	    java.sql.ResultSet rs, String catalog) throws SQLException {
	    byte[][] row = new byte[3][];
	    row[0] = rs.getBytes(1);
	    row[1] = s2b(SUPPORTS_FK);
	
	    String createTableString = rs.getString(2);
	    StringTokenizer lineTokenizer = new StringTokenizer(createTableString,
	            "\n");
	    StringBuffer commentBuf = new StringBuffer("comment; ");
	    boolean firstTime = true;
	
	    while (lineTokenizer.hasMoreTokens()) {
	        String line = lineTokenizer.nextToken().trim();
	
	        String constraintName = null;
	
	        if (StringUtils.startsWithIgnoreCase(line, "CONSTRAINT")) {
	            boolean usingBackTicks = true;
	            int beginPos = line.indexOf("`");
	
	            if (beginPos == -1) {
	                beginPos = line.indexOf("\"");
	                usingBackTicks = false;
	            }
	
	            if (beginPos != -1) {
	                int endPos = -1;
	
	                if (usingBackTicks) {
	                    endPos = line.indexOf("`", beginPos + 1);
	                } else {
	                    endPos = line.indexOf("\"", beginPos + 1);
	                }
	
	                if (endPos != -1) {
	                    constraintName = line.substring(beginPos + 1, endPos);
	                    line = line.substring(endPos + 1, line.length()).trim();
	                }
	            }
	        }
	
	        if (line.startsWith("FOREIGN KEY")) {
	            if (line.endsWith(",")) {
	                line = line.substring(0, line.length() - 1);
	            }
	
	            // Remove all back-ticks
	            int lineLength = line.length();
	            StringBuffer lineBuf = new StringBuffer(lineLength);
	
	            for (int i = 0; i < lineLength; i++) {
	                char c = line.charAt(i);
	
	                if (c != '`') {
	                    lineBuf.append(c);
	                }
	            }
	
	            line = lineBuf.toString();
	
	            StringTokenizer keyTokens = new StringTokenizer(line, "()",
	                    false);
	            keyTokens.nextToken(); // eat 'FOREIGN KEY'
	
	            String localColumnNamesString = keyTokens.nextToken();
	            String referCatalogTableString = keyTokens.nextToken();
	            StringTokenizer referSchemaTable = new StringTokenizer(referCatalogTableString.trim(),
	                    " .");
	            String referColumnNamesString = keyTokens.nextToken();
	            referSchemaTable.nextToken(); //discard the REFERENCES token
	
	            int numTokensLeft = referSchemaTable.countTokens();
	
	            String referCatalog = null;
	            String referTable = null;
	
	            if (numTokensLeft == 2) {
	                // some versions of MySQL don't report the database name
	                referCatalog = referSchemaTable.nextToken();
	                referTable = referSchemaTable.nextToken();
	            } else {
	                referTable = referSchemaTable.nextToken();
	                referCatalog = catalog;
	            }
	
	            if (!firstTime) {
	                commentBuf.append("; ");
	            } else {
	                firstTime = false;
	            }
	
	            if (constraintName != null) {
	                commentBuf.append(constraintName);
	            } else {
	                commentBuf.append("not_available");
	            }
	
	            commentBuf.append("(");
	            commentBuf.append(localColumnNamesString);
	            commentBuf.append(") REFER ");
	            commentBuf.append(referCatalog);
	            commentBuf.append("/");
	            commentBuf.append(referTable);
	            commentBuf.append("(");
	            commentBuf.append(referColumnNamesString);
	            commentBuf.append(")");
	
	            int lastParenIndex = line.lastIndexOf(")");
	
	            if (lastParenIndex != (line.length() - 1)) {
	                String cascadeOptions = cascadeOptions = line.substring(lastParenIndex +
	                            1);
	                commentBuf.append(" ");
	                commentBuf.append(cascadeOptions);
	            }
	        }
	    }
	
	    row[2] = s2b(commentBuf.toString());
	    rows.add(row);
	
	    return rows;
	}

	/**
	 * Creates a result set similar enough to 'SHOW TABLE STATUS' to allow the
	 * same code to work on extracting the foreign key data
	 *
	 * @param connToUse the database connection to use
	 * @param metadata the DatabaseMetaData instance calling this method
	 * @param catalog the database name to extract foreign key info for
	 * @param tableName the table to extract foreign key info for
	 *
	 * @return A result set that has the structure of 'show table status'
	 *
	 * @throws SQLException if a database access error occurs.
	 */
	private ResultSet extractForeignKeyFromCreateTable(String catalog, String tableName) throws SQLException {
	    ArrayList tableList = new ArrayList();
	    java.sql.ResultSet rs = null;
	    java.sql.Statement stmt = null;
	
	    if (tableName != null) {
	        tableList.add(tableName);
	    } else {
	        try {
	            rs = getTables(catalog, "", "%",
	                    new String[] { "TABLE" });
	
	            while (rs.next()) {
	                tableList.add(rs.getString("TABLE_NAME"));
	            }
	        } finally {
	            if (rs != null) {
	                rs.close();
	            }
	
	            rs = null;
	        }
	    }
	
	    ArrayList rows = new ArrayList();
	    Field[] fields = new Field[3];
	    fields[0] = new Field("", "Name", Types.CHAR, Integer.MAX_VALUE);
	    fields[1] = new Field("", "Type", Types.CHAR, 255);
	    fields[2] = new Field("", "Comment", Types.CHAR, Integer.MAX_VALUE);
	
	    int numTables = tableList.size();
	    stmt = this.conn.createStatement();
	    stmt.setEscapeProcessing(false);
	
	    try {
	        for (int i = 0; i < numTables; i++) {
	            String tableToExtract = (String) tableList.get(i);
	
	            String query = new StringBuffer("SHOW CREATE TABLE ").append(
	                    "`").append(catalog).append("`.`").append(tableToExtract)
	                                                                 .append("`")
	                                                                 .toString();
	            rs = stmt.executeQuery(query);
	
	            while (rs.next()) {
	                extractForeignKeyForTable(rows, rs, catalog);
	            }
	        }
	    } finally {
	        if (rs != null) {
	            rs.close();
	        }
	
	        rs = null;
	
	        if (stmt != null) {
	            stmt.close();
	        }
	
	        stmt = null;
	    }
	
	    return buildResultSet(fields, rows);
	}

	/*
	 *     * Each row in the ResultSet is a parameter desription or column
	 * description with the following fields:
	 *
	 * <OL>
	 * <li>
	 * <B>PROCEDURE_CAT</B> String => procedure catalog (may be null)
	 * </li>
	 * <li>
	 * <B>PROCEDURE_SCHEM</B> String => procedure schema (may be null)
	 * </li>
	 * <li>
	 * <B>PROCEDURE_NAME</B> String => procedure name
	 * </li>
	 * <li>
	 * <B>COLUMN_NAME</B> String => column/parameter name
	 * </li>
	 * <li>
	 * <B>COLUMN_TYPE</B> Short => kind of column/parameter:
	 *
	 * <UL>
	 * <li>
	 * procedureColumnUnknown - nobody knows
	 * </li>
	 * <li>
	 * procedureColumnIn - IN parameter
	 * </li>
	 * <li>
	 * procedureColumnInOut - INOUT parameter
	 * </li>
	 * <li>
	 * procedureColumnOut - OUT parameter
	 * </li>
	 * <li>
	 * procedureColumnReturn - procedure return value
	 * </li>
	 * <li>
	 * procedureColumnResult - result column in ResultSet
	 * </li>
	 * </ul>
	 *
	 * </li>
	 * <li>
	 * <B>DATA_TYPE</B> short => SQL type from java.sql.Types
	 * </li>
	 * <li>
	 * <B>TYPE_NAME</B> String => SQL type name
	 * </li>
	 * <li>
	 * <B>PRECISION</B> int => precision
	 * </li>
	 * <li>
	 * <B>LENGTH</B> int => length in bytes of data
	 * </li>
	 * <li>
	 * <B>SCALE</B> short => scale
	 * </li>
	 * <li>
	 * <B>RADIX</B> short => radix
	 * </li>
	 * <li>
	 * <B>NULLABLE</B> short => can it contain NULL?
	 *
	 * <UL>
	 * <li>
	 * procedureNoNulls - does not allow NULL values
	 * </li>
	 * <li>
	 * procedureNullable - allows NULL values
	 * </li>
	 * <li>
	 * procedureNullableUnknown - nullability unknown
	 * </li>
	 * </ul>
	 *
	 * </li>
	 * <li>
	 * <B>REMARKS</B> String => comment describing parameter/column
	 * </li>
	 * </ol>
	 * </p>
	 *
	 * <P>
	 * <B>Note:</B> Some databases may not return the column descriptions for a
	 * procedure. Additional columns beyond REMARKS can be defined by the
	 * database.
	 * </p>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog
	 * @param schemaPattern a schema name pattern; "" retrieves those without a
	 *        schema
	 * @param procedureNamePattern a procedure name pattern
	 * @param columnNamePattern a column name pattern
	 *
	 * @return ResultSet each row is a stored procedure parameter or column
	 *         description
	 *
	 * @throws SQLException if a database access error occurs
	 *
	 * @see #getSearchStringEscape
	 */
	private void getCallStmtParameterTypes(String procName,
	    String parameterNamePattern, List resultRows) throws SQLException {
	    java.sql.Statement paramRetrievalStmt = null;
	    java.sql.ResultSet paramRetrievalRs = null;
	
	    byte[] procNameAsBytes = null;
	
	    try {
	        procNameAsBytes = procName.getBytes("UTF-8");
	    } catch (UnsupportedEncodingException ueEx) {
	        procNameAsBytes = s2b(procName);
	
	        // Set all fields to connection encoding
	    }
	
	    // First try 'select from mysql.proc, as this is easier to parse...
	    String parameterDef = null;
	
	    PreparedStatement paramRetrievalPreparedStatement = null;
	
	    try {
	        paramRetrievalPreparedStatement = this.conn.clientPrepareStatement(
	                "SELECT param_list FROM mysql.proc WHERE name=?");
	
	        if (paramRetrievalPreparedStatement.getMaxRows() != 0) {
	            paramRetrievalPreparedStatement.setMaxRows(0);
	        }
	
	        paramRetrievalPreparedStatement.setString(1, procName);
	
	        paramRetrievalRs = paramRetrievalPreparedStatement.executeQuery();
	
	        if (paramRetrievalRs.next()) {
	            parameterDef = paramRetrievalRs.getString(1);
	        }
	    } catch (SQLException sqlEx) {
	        //
	        // Okay, try the less precise route :(
	        //
	        paramRetrievalStmt = this.conn.createStatement();
	
	        if (paramRetrievalStmt.getMaxRows() != 0) {
	            paramRetrievalStmt.setMaxRows(0);
	        }
	
	        paramRetrievalRs = paramRetrievalStmt.executeQuery(
	                "SHOW CREATE PROCEDURE " + procName);
	
	        if (paramRetrievalRs.next()) {
	            String procedureDef = paramRetrievalRs.getString(
	                    "Create Procedure");
	
	            int openParenIndex = procedureDef.indexOf('(');
	
	            String beforeBegin = null;
	
	            // Try and fudge this with the 'begin' statement
	            int beginIndex = StringUtils.indexOfIgnoreCase(procedureDef,
	                    "\nbegin");
	
	            
	            // Bah, we _really_ need information schema here
	            
	            if (beginIndex != -1) {
	                beforeBegin = procedureDef.substring(0, beginIndex);
	            } else {
	            	beginIndex = StringUtils.indexOfIgnoreCase(procedureDef, "\n");
	            	
	            	if (beginIndex != -1) {
	                    beforeBegin = procedureDef.substring(0, beginIndex);
	            	} else {
	            		throw new SQLException("Driver requires declaration of procedure to either contain a '\\nbegin' or '\\n' to follow argument declaration, or SELECT privilege on mysql.proc to parse column types.", 
	            				SQLError.SQL_STATE_GENERAL_ERROR);
	            	}
	                
	            }
	
	            int endParenIndex = beforeBegin.lastIndexOf(')');
	
	            if ((openParenIndex == -1) || (endParenIndex == -1)) {
	                // parse error?
	                throw new SQLException("Internal error when parsing callable statement metadata",
	                    SQLError.SQL_STATE_GENERAL_ERROR);
	            }
	
	            parameterDef = procedureDef.substring(openParenIndex + 1,
	                    endParenIndex);
	        }
	    } finally {
	        SQLException sqlExRethrow = null;
	
	        if (paramRetrievalRs != null) {
	            try {
	                paramRetrievalRs.close();
	            } catch (SQLException sqlEx) {
	                sqlExRethrow = sqlEx;
	            }
	
	            paramRetrievalRs = null;
	        }
	
	        if (paramRetrievalPreparedStatement != null) {
	            try {
	                paramRetrievalPreparedStatement.close();
	            } catch (SQLException sqlEx) {
	                sqlExRethrow = sqlEx;
	            }
	
	            paramRetrievalPreparedStatement = null;
	        }
	
	        if (paramRetrievalStmt != null) {
	            try {
	                paramRetrievalStmt.close();
	            } catch (SQLException sqlEx) {
	                sqlExRethrow = sqlEx;
	            }
	
	            paramRetrievalStmt = null;
	        }
	
	        if (sqlExRethrow != null) {
	            throw sqlExRethrow;
	        }
	    }
	
	    if (parameterDef != null) {
	        List parseList = StringUtils.split(parameterDef, ",", true);
	
	        int parseListLen = parseList.size();
	
	        for (int i = 0; i < parseListLen; i++) {
	            String declaration = (String) parseList.get(i);
	
	            StringTokenizer declarationTok = new StringTokenizer(declaration,
	                    " \t");
	
	            String paramName = null;
	            boolean isOutParam = false;
	            boolean isInParam = false;
	
	            if (declarationTok.hasMoreTokens()) {
	                String possibleParamName = declarationTok.nextToken();
	
	                if (possibleParamName.equalsIgnoreCase("OUT")) {
	                    isOutParam = true;
	
	                    if (declarationTok.hasMoreTokens()) {
	                        paramName = declarationTok.nextToken();
	                    } else {
	                        throw new SQLException("Internal error when parsing callable statement metadata (missing parameter name)",
	                            SQLError.SQL_STATE_GENERAL_ERROR);
	                    }
	                } else if (possibleParamName.equalsIgnoreCase("INOUT")) {
	                    isOutParam = true;
	                    isInParam = true;
	
	                    if (declarationTok.hasMoreTokens()) {
	                        paramName = declarationTok.nextToken();
	                    } else {
	                        throw new SQLException("Internal error when parsing callable statement metadata (missing parameter name)",
	                            SQLError.SQL_STATE_GENERAL_ERROR);
	                    }
	                } else if (possibleParamName.equalsIgnoreCase("IN")) {
	                    isOutParam = false;
	                    isInParam = true;
	
	                    if (declarationTok.hasMoreTokens()) {
	                        paramName = declarationTok.nextToken();
	                    } else {
	                        throw new SQLException("Internal error when parsing callable statement metadata (missing parameter name)",
	                            SQLError.SQL_STATE_GENERAL_ERROR);
	                    }
	                } else {
	                    isOutParam = false;
	                    isInParam = true;
	
	                    paramName = possibleParamName;
	                }
	
	                TypeDescriptor typeDesc = null;
	
	                if (declarationTok.hasMoreTokens()) {
	                    StringBuffer typeInfoBuf = new StringBuffer(declarationTok.nextToken());
	
	                    while (declarationTok.hasMoreTokens()) {
	                        typeInfoBuf.append(declarationTok.nextToken());
	                    }
	
	                    String typeInfo = typeInfoBuf.toString();
	
	                    typeDesc = new TypeDescriptor(this.conn, typeInfo, null);
	                } else {
	                    throw new SQLException("Internal error when parsing callable statement metadata (missing parameter type)",
	                        SQLError.SQL_STATE_GENERAL_ERROR);
	                }
	
	                int wildCompareRes = StringUtils.wildCompare(paramName,
	                        parameterNamePattern);
	
	                if (wildCompareRes != StringUtils.WILD_COMPARE_NO_MATCH) {
	                    byte[][] row = new byte[14][];
	
	                    row[0] = null; // PROCEDURE_CAT
	                    row[1] = null; // PROCEDURE_SCHEM
	                    row[2] = procNameAsBytes; // PROCEDURE/NAME
	                    row[3] = s2b(paramName); // COLUMN_NAME
	
	                    // COLUMN_TYPE 
	                    if (isInParam && isOutParam) {
	                        row[4] = s2b(String.valueOf(DatabaseMetaData.procedureColumnInOut));
	                    } else if (isInParam) {
	                        row[4] = s2b(String.valueOf(DatabaseMetaData.procedureColumnIn));
	                    } else if (isOutParam) {
	                        row[4] = s2b(String.valueOf(DatabaseMetaData.procedureColumnOut));
	                    } else {
	                        row[4] = s2b(String.valueOf(DatabaseMetaData.procedureColumnUnknown));
	                    }
	
	                    row[5] = s2b(Short.toString(typeDesc.dataType)); // DATA_TYPE
	                    row[6] = s2b(typeDesc.typeName); // TYPE_NAME
	                    row[7] = s2b(Integer.toString(typeDesc.columnSize)); // PRECISION
	                    row[8] = s2b(Integer.toString(typeDesc.bufferLength)); // LENGTH
	                    row[9] = s2b(Integer.toString(typeDesc.decimalDigits)); // SCALE
	                    row[10] = s2b(Integer.toString(typeDesc.numPrecRadix)); // RADIX
	
	                    // Map 'column****' to 'procedure****'
	                    switch (typeDesc.nullability) {
	                    case DatabaseMetaData.columnNoNulls:
	                        row[11] = s2b(Integer.toString(DatabaseMetaData.procedureNoNulls)); // NULLABLE
	
	                        break;
	
	                    case DatabaseMetaData.columnNullable:
	                        row[11] = s2b(Integer.toString(DatabaseMetaData.procedureNullable)); // NULLABLE
	
	                        break;
	
	                    case DatabaseMetaData.columnNullableUnknown:
	                        row[11] = s2b(Integer.toString(
	                        		DatabaseMetaData.procedureNullableUnknown)); // nullable
	
	                        break;
	
	                    default:
	                        throw new SQLException("Internal error while parsing callable statement metadata (unknown nullability value fount)",
	                            SQLError.SQL_STATE_GENERAL_ERROR);
	                    }
	
	                    row[12] = null; //REMARKS
	
	                    resultRows.add(row);
	                }
	            } else {
	                throw new SQLException("Internal error when parsing callable statement metadata (unknown output from 'SHOW CREATE PROCEDURE')",
	                    SQLError.SQL_STATE_GENERAL_ERROR);
	            }
	        }
	    } else {
	        // Is this an error? JDBC spec doesn't make it clear if stored procedure doesn't
	        // exist, is it an error....
	    }
	}

}
