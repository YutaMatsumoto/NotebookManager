/*
 * Created on Nov 9, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.mysql.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author mmatthew
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
interface SchemaData {
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
	public abstract java.sql.ResultSet getColumnPrivileges(String catalog,
			String schema, String table, String columnNamePattern)
			throws SQLException;

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
	public abstract java.sql.ResultSet getColumns(String catalog,
			String schemaPattern, String tableName, String columnNamePattern)
			throws SQLException;

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
	public abstract java.sql.ResultSet getIndexInfo(String catalog,
			String schema, String table, boolean unique, boolean approximate)
			throws SQLException;

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
	public abstract java.sql.ResultSet getPrimaryKeys(String catalog,
			String schema, String table) throws SQLException;

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
	public abstract java.sql.ResultSet getProcedureColumns(String catalog,
			String schemaPattern, String procedureNamePattern,
			String columnNamePattern) throws SQLException;

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
	public abstract java.sql.ResultSet getProcedures(String catalog,
			String schemaPattern, String procedureNamePattern)
			throws SQLException;

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
	public abstract java.sql.ResultSet getSchemas() throws SQLException;

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
	public abstract java.sql.ResultSet getTablePrivileges(String catalog,
			String schemaPattern, String tableNamePattern) throws SQLException;

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
	public abstract java.sql.ResultSet getTables(String catalog,
			String schemaPattern, String tableNamePattern, String[] types)
			throws SQLException;

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
	public abstract java.sql.ResultSet getVersionColumns(String catalog,
			String schema, String table) throws SQLException;

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
	public abstract java.sql.ResultSet getCrossReference(String primaryCatalog,
			String primarySchema, String primaryTable, String foreignCatalog,
			String foreignSchema, String foreignTable) throws SQLException;

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
	public abstract java.sql.ResultSet getExportedKeys(String catalog,
			String schema, String table) throws SQLException;

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
	public abstract java.sql.ResultSet getImportedKeys(String catalog,
			String schema, String table) throws SQLException;
}