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
package testsuite.regression;

import com.mysql.jdbc.NonRegisteringDriver;
import com.mysql.jdbc.NotUpdatable;
import com.mysql.jdbc.SQLError;

import testsuite.BaseTestCase;

import java.io.Reader;
import java.math.BigDecimal;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Calendar;
import java.util.Properties;


/**
 * Regression test cases for the ResultSet class.
 *
 * @author Mark Matthews
 */
public class ResultSetRegressionTest extends BaseTestCase {
    /**
     * Creates a new ResultSetRegressionTest
     *
     * @param name the name of the test to run
     */
    public ResultSetRegressionTest(String name) {
        super(name);
    }

    /**
     * Runs all test cases in this test suite
     *
     * @param args
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(ResultSetRegressionTest.class);
    }

    /**
     * Tests fix for BUG#???? -- Numeric types and server-side prepared
     * statements incorrectly detect nulls.
     *
     * @throws Exception if the test fails
     */
    public void testBug2359() throws Exception {
        try {
            /*
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testBug2359");
            this.stmt.executeUpdate("CREATE TABLE testBug2359 (field1 INT) TYPE=InnoDB");
            this.stmt.executeUpdate("INSERT INTO testBug2359 VALUES (null), (1)");

            this.pstmt = this.conn.prepareStatement("SELECT field1 FROM testBug2359 WHERE field1 IS NULL");
            this.rs = this.pstmt.executeQuery();

            assertTrue(this.rs.next());

            assertTrue(this.rs.getByte(1) == 0);
            assertTrue(this.rs.wasNull());

            assertTrue(this.rs.getShort(1) == 0);
            assertTrue(this.rs.wasNull());

            assertTrue(this.rs.getInt(1) == 0);
            assertTrue(this.rs.wasNull());

            assertTrue(this.rs.getLong(1) == 0);
            assertTrue(this.rs.wasNull());

            assertTrue(this.rs.getFloat(1) == 0);
            assertTrue(this.rs.wasNull());

            assertTrue(this.rs.getDouble(1) == 0);
            assertTrue(this.rs.wasNull());

            assertTrue(this.rs.getBigDecimal(1) == null);
            assertTrue(this.rs.wasNull());

            this.rs.close();

            this.pstmt = this.conn.prepareStatement("SELECT max(field1) FROM testBug2359 WHERE field1 IS NOT NULL");
                                    this.rs = this.pstmt.executeQuery();
                                    assertTrue(this.rs.next());

                                    assertTrue(this.rs.getByte(1) == 1);
                                    assertTrue(!this.rs.wasNull());

                                    assertTrue(this.rs.getShort(1) == 1);
                                    assertTrue(!this.rs.wasNull());

                                    assertTrue(this.rs.getInt(1) == 1);
                                    assertTrue(!this.rs.wasNull());

                                    assertTrue(this.rs.getLong(1) == 1);
                                    assertTrue(!this.rs.wasNull());

                                    assertTrue(this.rs.getFloat(1) == 1);
                                    assertTrue(!this.rs.wasNull());

                                    assertTrue(this.rs.getDouble(1) == 1);
                                    assertTrue(!this.rs.wasNull());

                                    assertTrue(this.rs.getBigDecimal(1) != null);
                                    assertTrue(!this.rs.wasNull());

                                    */
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testBug2359_1");
            this.stmt.executeUpdate(
                "CREATE TABLE testBug2359_1 (id INT) TYPE=InnoDB");
            this.stmt.executeUpdate("INSERT INTO testBug2359_1 VALUES (1)");

            this.pstmt = this.conn.prepareStatement("SELECT max(id) FROM testBug2359_1");
            this.rs = this.pstmt.executeQuery();

            if (this.rs.next()) {
                assertTrue(this.rs.getInt(1) != 0);
                this.rs.close();
            }

            this.rs.close();
        } finally {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testBug2359_1");
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testBug2359");

            this.rs.close();
            this.pstmt.close();
        }
    }

    /**
     * Tests fix for BUG#2643, ClassCastException when using this.rs.absolute() and
     * server-side prepared statements.
     *
     * @throws Exception
     */
    public void testBug2623() throws Exception {
        PreparedStatement pStmt = null;

        try {
            pStmt = this.conn.prepareStatement("SELECT NOW()",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            this.rs = pStmt.executeQuery();

            this.rs.absolute(1);
        } finally {
            if (this.rs != null) {
                this.rs.close();
            }

            this.rs = null;

            if (pStmt != null) {
                pStmt.close();
            }
        }
    }

    /**
     * Tests fix for BUG#2654, "Column 'column.table' not found" when "order
     * by" in query"
     *
     * @throws Exception if the test fails
     */
    public void testBug2654() throws Exception {
        if (false) { // this is currently a server-level bug

            try {
                this.stmt.executeUpdate("DROP TABLE IF EXISTS foo");
                this.stmt.executeUpdate("DROP TABLE IF EXISTS bar");

                this.stmt.executeUpdate("CREATE TABLE foo (" +
                    "  id tinyint(3) default NULL," +
                    "  data varchar(255) default NULL" +
                    ") TYPE=MyISAM DEFAULT CHARSET=latin1");
                this.stmt.executeUpdate(
                    "INSERT INTO foo VALUES (1,'male'),(2,'female')");

                this.stmt.executeUpdate("CREATE TABLE bar (" +
                    "id tinyint(3) unsigned default NULL," +
                    "data char(3) default '0'" +
                    ") TYPE=MyISAM DEFAULT CHARSET=latin1");

                this.stmt.executeUpdate(
                    "INSERT INTO bar VALUES (1,'yes'),(2,'no')");

                String statement = "select foo.id, foo.data, " +
                    "bar.data from foo, bar" + "	where " +
                    "foo.id = bar.id order by foo.id";

                String column = "foo.data";

                this.rs = this.stmt.executeQuery(statement);

                ResultSetMetaData rsmd = this.rs.getMetaData();
                System.out.println(rsmd.getTableName(1));
                System.out.println(rsmd.getColumnName(1));

                this.rs.next();

                String fooData = this.rs.getString(column);
            } finally {
                this.stmt.executeUpdate("DROP TABLE IF EXISTS foo");
                this.stmt.executeUpdate("DROP TABLE IF EXISTS bar");
            }
        }
    }

    /**
     * Tests for fix to BUG#1130
     *
     * @throws Exception if the test fails
     */
    public void testClobTruncate() throws Exception {
        try {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testClobTruncate");
            this.stmt.executeUpdate(
                "CREATE TABLE testClobTruncate (field1 TEXT)");
            this.stmt.executeUpdate(
                "INSERT INTO testClobTruncate VALUES ('abcdefg')");

            this.rs = this.stmt.executeQuery("SELECT * FROM testClobTruncate");
            this.rs.next();

            Clob clob = this.rs.getClob(1);
            clob.truncate(3);

            Reader reader = clob.getCharacterStream();
            char[] buf = new char[8];
            int charsRead = reader.read(buf);

            String clobAsString = new String(buf, 0, charsRead);

            assertTrue(clobAsString.equals("abc"));
        } finally {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testClobTruncate");
        }
    }

    /**
     * Tests that streaming result sets are registered correctly.
     *
     * @throws Exception if any errors occur
     */
    public void testClobberStreamingRS() throws Exception {
        try {
            Properties props = new Properties();
            props.setProperty("clobberStreamingResults", "true");

            Connection clobberConn = getConnectionWithProps(props);

            Statement clobberStmt = clobberConn.createStatement();

            clobberStmt.executeUpdate("DROP TABLE IF EXISTS StreamingClobber");
            clobberStmt.executeUpdate(
                "CREATE TABLE StreamingClobber ( DUMMYID " +
                " INTEGER NOT NULL, DUMMYNAME VARCHAR(32),PRIMARY KEY (DUMMYID) )");
            clobberStmt.executeUpdate(
                "INSERT INTO StreamingClobber (DUMMYID, DUMMYNAME) VALUES (0, NULL)");
            clobberStmt.executeUpdate(
                "INSERT INTO StreamingClobber (DUMMYID, DUMMYNAME) VALUES (1, 'nro 1')");
            clobberStmt.executeUpdate(
                "INSERT INTO StreamingClobber (DUMMYID, DUMMYNAME) VALUES (2, 'nro 2')");
            clobberStmt.executeUpdate(
                "INSERT INTO StreamingClobber (DUMMYID, DUMMYNAME) VALUES (3, 'nro 3')");

            Statement streamStmt = null;

            try {
                streamStmt = clobberConn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                        java.sql.ResultSet.CONCUR_READ_ONLY);
                streamStmt.setFetchSize(Integer.MIN_VALUE);

                this.rs = streamStmt.executeQuery(
                        "SELECT DUMMYID, DUMMYNAME " +
                        "FROM StreamingClobber ORDER BY DUMMYID");

                this.rs.next();

                // This should proceed normally, after the driver 
                // clears the input stream 
                clobberStmt.executeQuery("SHOW VARIABLES");
                this.rs.close();
            } finally {
                if (streamStmt != null) {
                    streamStmt.close();
                }
            }
        } finally {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS StreamingClobber");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testEmptyResultSetGet() throws Exception {
        try {
            this.rs = this.stmt.executeQuery("SHOW VARIABLES LIKE 'foo'");
            System.out.println(this.rs.getInt(1));
        } catch (SQLException sqlEx) {
            assertTrue("Correct exception not thrown",
                SQLError.SQL_STATE_GENERAL_ERROR.equals(sqlEx.getSQLState()));
        }
    }

    /**
     * Checks fix for BUG#1592 -- cross-database updatable result sets are not
     * checked for updatability correctly.
     *
     * @throws Exception if the test fails.
     */
    public void testFixForBug1592() throws Exception {
        if (versionMeetsMinimum(4, 1)) {
            Statement updatableStmt = this.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            try {
                updatableStmt.execute("SELECT * FROM mysql.user");

                this.rs = updatableStmt.getResultSet();
            } catch (SQLException sqlEx) {
                String message = sqlEx.getMessage();

                if ((message != null) && (message.indexOf("denied") != -1)) {
                    System.err.println(
                        "WARN: Can't complete testFixForBug1592(), access to" +
                        " 'mysql' database not allowed");
                } else {
                    throw sqlEx;
                }
            }
        }
    }

    /**
     * Tests fix for BUG#2006, where 2 columns with same name in a result set
     * are returned via findColumn() in the wrong order...The JDBC spec
     * states,  that the _first_ matching column should be returned.
     *
     * @throws Exception if the test fails
     */
    public void testFixForBug2006() throws Exception {
        try {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testFixForBug2006_1");
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testFixForBug2006_2");
            this.stmt.executeUpdate(
                "CREATE TABLE testFixForBug2006_1 (key_field INT NOT NULL)");
            this.stmt.executeUpdate(
                "CREATE TABLE testFixForBug2006_2 (key_field INT NULL)");
            this.stmt.executeUpdate(
                "INSERT INTO testFixForBug2006_1 VALUES (1)");

            this.rs = this.stmt.executeQuery(
                    "SELECT testFixForBug2006_1.key_field, testFixForBug2006_2.key_field FROM testFixForBug2006_1 LEFT JOIN testFixForBug2006_2 USING(key_field)");

            ResultSetMetaData rsmd = this.rs.getMetaData();

            assertTrue(rsmd.getColumnName(1).equals(rsmd.getColumnName(2)));
            assertTrue(rsmd.isNullable(this.rs.findColumn("key_field")) == ResultSetMetaData.columnNoNulls);
            assertTrue(rsmd.isNullable(2) == ResultSetMetaData.columnNullable);
            assertTrue(this.rs.next());
            assertTrue(this.rs.getObject(1) != null);
            assertTrue(this.rs.getObject(2) == null);
        } finally {
            if (this.rs != null) {
                try {
                    this.rs.close();
                } catch (SQLException sqlEx) {
                    // ignore
                }

                this.rs = null;
            }

            this.stmt.executeUpdate("DROP TABLE IF EXISTS testFixForBug2006_1");
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testFixForBug2006_2");
        }
    }

    /**
     * Tests that ResultSet.getLong() does not truncate values.
     *
     * @throws Exception if any errors occur
     */
    public void testGetLongBug() throws Exception {
        this.stmt.executeUpdate("DROP TABLE IF EXISTS getLongBug");
        this.stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS getLongBug (int_col int, bigint_col bigint)");

        int intVal = 123456;
        long longVal1 = 123456789012345678L;
        long longVal2 = -2079305757640172711L;
        this.stmt.executeUpdate("INSERT INTO getLongBug " +
            "(int_col, bigint_col) " + "VALUES (" + intVal + ", " + longVal1 +
            "), " + "(" + intVal + ", " + longVal2 + ")");

        try {
            this.rs = this.stmt.executeQuery(
                    "SELECT int_col, bigint_col FROM getLongBug ORDER BY bigint_col DESC");
            this.rs.next();
            assertTrue("Values not decoded correctly",
                ((this.rs.getInt(1) == intVal) &&
                (this.rs.getLong(2) == longVal1)));
            this.rs.next();
            assertTrue("Values not decoded correctly",
                ((this.rs.getInt(1) == intVal) &&
                (this.rs.getLong(2) == longVal2)));
        } finally {
            if (this.rs != null) {
                try {
                    this.rs.close();
                } catch (Exception ex) {
                    // ignore
                }
            }

            this.stmt.executeUpdate("DROP TABLE IF EXISTS getLongBug");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testGetTimestampWithDate() throws Exception {
        try {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testGetTimestamp");
            this.stmt.executeUpdate("CREATE TABLE testGetTimestamp (d date)");
            this.stmt.executeUpdate(
                "INSERT INTO testGetTimestamp values (now())");

            this.rs = this.stmt.executeQuery("SELECT * FROM testGetTimestamp");
            this.rs.next();
            System.out.println(this.rs.getTimestamp(1));
        } finally {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testGetTimestamp");
        }
    }

    /**
     * Tests a bug where ResultSet.isBefireFirst() would return true when the
     * result set was empty (which is incorrect)
     *
     * @throws Exception if an error occurs.
     */
    public void testIsBeforeFirstOnEmpty() throws Exception {
        try {
            //Query with valid rows: isBeforeFirst() correctly returns True
            this.rs = this.stmt.executeQuery("SHOW VARIABLES LIKE 'version'");
            assertTrue("Non-empty search should return true", this.rs.isBeforeFirst());

            //Query with empty result: isBeforeFirst() falsely returns True
            //Sun's documentation says it should return false
            this.rs = this.stmt.executeQuery("SHOW VARIABLES LIKE 'garbage'");
            assertTrue("Empty search should return false ", !this.rs.isBeforeFirst());
        } finally {
            this.rs.close();
        }
    }

    /**
     * Tests a bug where ResultSet.isBefireFirst() would return true when the
     * result set was empty (which is incorrect)
     *
     * @throws Exception if an error occurs.
     */
    public void testMetaDataIsWritable() throws Exception {
        try {
            //Query with valid rows
            this.rs = this.stmt.executeQuery("SHOW VARIABLES LIKE 'version'");

            ResultSetMetaData rsmd = this.rs.getMetaData();

            int numColumns = rsmd.getColumnCount();

            for (int i = 1; i <= numColumns; i++) {
                assertTrue("rsmd.isWritable() should != rsmd.isReadOnly()",
                    rsmd.isWritable(i) != rsmd.isReadOnly(i));
            }
        } finally {
            this.rs.close();
        }
    }

    /**
     * Tests fix for bug # 496
     *
     * @throws Exception if an error happens.
     */
    public void testNextAndPrevious() throws Exception {
        try {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testNextAndPrevious");
            this.stmt.executeUpdate("CREATE TABLE testNextAndPrevious (field1 int)");
            this.stmt.executeUpdate("INSERT INTO testNextAndPrevious VALUES (1)");

            this.rs = this.stmt.executeQuery("SELECT * from testNextAndPrevious");

            System.out.println("Currently at row " + this.rs.getRow());
            this.rs.next();
            System.out.println("Value at row " + this.rs.getRow() + " is " +
                this.rs.getString(1));

            this.rs.previous();

            try {
                System.out.println("Value at row " + this.rs.getRow() + " is " +
                    this.rs.getString(1));
                fail(
                    "Should not be able to retrieve values with invalid cursor");
            } catch (SQLException sqlEx) {
                assertTrue(sqlEx.getMessage().startsWith("Before start"));
            }

            this.rs.next();

            this.rs.next();

            try {
                System.out.println("Value at row " + this.rs.getRow() + " is " +
                    this.rs.getString(1));
                fail(
                    "Should not be able to retrieve values with invalid cursor");
            } catch (SQLException sqlEx) {
                assertTrue(sqlEx.getMessage().startsWith("After end"));
            }
        } finally {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testNextAndPrevious");
        }
    }

    /**
     * Tests fix for BUG#1630 (not updatable exception turning into NPE on
     * second updateFoo() method call.
     *
     * @throws Exception if an unexpected exception is thrown.
     */
    public void testNotUpdatable() throws Exception {
        this.rs = null;

        try {
            String sQuery = "SHOW VARIABLES";
            this.pstmt = this.conn.prepareStatement(sQuery,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            this.rs = this.pstmt.executeQuery();

            if (this.rs.next()) {
                this.rs.absolute(1);

                try {
                    this.rs.updateInt(1, 1);
                } catch (SQLException sqlEx) {
                    assertTrue(sqlEx instanceof NotUpdatable);
                }

                try {
                    this.rs.updateString(1, "1");
                } catch (SQLException sqlEx) {
                    assertTrue(sqlEx instanceof NotUpdatable);
                }
            }
        } finally {
            if (this.pstmt != null) {
                try {
                	this.pstmt.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    /**
     * Tests that streaming result sets are registered correctly.
     *
     * @throws Exception if any errors occur
     */
    public void testStreamingRegBug() throws Exception {
        try {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS StreamingRegBug");
            this.stmt.executeUpdate("CREATE TABLE StreamingRegBug ( DUMMYID " +
                " INTEGER NOT NULL, DUMMYNAME VARCHAR(32),PRIMARY KEY (DUMMYID) )");
            this.stmt.executeUpdate(
                "INSERT INTO StreamingRegBug (DUMMYID, DUMMYNAME) VALUES (0, NULL)");
            this.stmt.executeUpdate(
                "INSERT INTO StreamingRegBug (DUMMYID, DUMMYNAME) VALUES (1, 'nro 1')");
            this.stmt.executeUpdate(
                "INSERT INTO StreamingRegBug (DUMMYID, DUMMYNAME) VALUES (2, 'nro 2')");
            this.stmt.executeUpdate(
                "INSERT INTO StreamingRegBug (DUMMYID, DUMMYNAME) VALUES (3, 'nro 3')");

            PreparedStatement streamStmt = null;

            try {
                streamStmt = this.conn.prepareStatement("SELECT DUMMYID, DUMMYNAME " +
                        "FROM StreamingRegBug ORDER BY DUMMYID",
                        java.sql.ResultSet.TYPE_FORWARD_ONLY,
                        java.sql.ResultSet.CONCUR_READ_ONLY);
                streamStmt.setFetchSize(Integer.MIN_VALUE);

                this.rs = streamStmt.executeQuery();

                while (this.rs.next()) {
                    this.rs.getString(1);
                }

                this.rs.close(); // error occurs here
            } catch (SQLException sqlEx) {
            	
            } finally {
                if (streamStmt != null) {
                	try {
                		streamStmt.close();
                	} catch (SQLException exWhileClose) {
                		exWhileClose.printStackTrace();
                	}
                }
            }
        } finally {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS StreamingRegBug");
        }
    }

    /**
     * Tests that result sets can be updated when all parameters are correctly
     * set.
     *
     * @throws Exception if any errors occur
     */
    public void testUpdatability() throws Exception {
        this.rs = null;

        this.stmt.execute("DROP TABLE IF EXISTS updatabilityBug");
        this.stmt.execute("CREATE TABLE IF NOT EXISTS updatabilityBug (" +
            " id int(10) unsigned NOT NULL auto_increment," +
            " field1 varchar(32) NOT NULL default ''," +
            " field2 varchar(128) NOT NULL default ''," +
            " field3 varchar(128) default NULL," +
            " field4 varchar(128) default NULL," +
            " field5 varchar(64) default NULL," +
            " field6 int(10) unsigned default NULL," +
            " field7 varchar(64) default NULL," + " PRIMARY KEY  (id)" +
            ") TYPE=InnoDB;");
        this.stmt.executeUpdate("insert into updatabilityBug (id) values (1)");

        try {
            String sQuery = " SELECT * FROM updatabilityBug WHERE id = ? ";
            this.pstmt = this.conn.prepareStatement(sQuery,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            this.conn.setAutoCommit(false);
            this.pstmt.setInt(1, 1);
            this.rs = this.pstmt.executeQuery();

            if (this.rs.next()) {
                this.rs.absolute(1);
                this.rs.updateInt("id", 1);
                this.rs.updateString("field1", "1");
                this.rs.updateString("field2", "1");
                this.rs.updateString("field3", "1");
                this.rs.updateString("field4", "1");
                this.rs.updateString("field5", "1");
                this.rs.updateInt("field6", 1);
                this.rs.updateString("field7", "1");
                this.rs.updateRow();
            }

            this.conn.commit();
            this.conn.setAutoCommit(true);
        } finally {
            if (this.pstmt != null) {
                try {
                	this.pstmt.close();
                } catch (Exception e) {
                    // ignore
                }
            }

            this.stmt.execute("DROP TABLE IF EXISTS updatabilityBug");
        }
    }

    /**
     * Test fixes for BUG#1071
     *
     * @throws Exception if the test fails.
     */
    public void testUpdatabilityAndEscaping() throws Exception {
        Properties props = new Properties();
        props.setProperty("useUnicode", "true");
        props.setProperty("characterEncoding", "big5");

        Connection updConn = getConnectionWithProps(props);
        Statement updStmt = updConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);

        try {
            updStmt.executeUpdate(
                "DROP TABLE IF EXISTS testUpdatesWithEscaping");
            updStmt.executeUpdate(
                "CREATE TABLE testUpdatesWithEscaping (field1 INT PRIMARY KEY, field2 VARCHAR(64))");
            updStmt.executeUpdate(
                "INSERT INTO testUpdatesWithEscaping VALUES (1, null)");

            String stringToUpdate = "\" \\ '";

            this.rs = updStmt.executeQuery(
                    "SELECT * from testUpdatesWithEscaping");

            this.rs.next();
            this.rs.updateString(2, stringToUpdate);
            this.rs.updateRow();

            assertTrue(stringToUpdate.equals(this.rs.getString(2)));
        } finally {
            updStmt.executeUpdate(
                "DROP TABLE IF EXISTS testUpdatesWithEscaping");
            updStmt.close();
            updConn.close();
        }
    }

    /**
     * Tests the fix for BUG#661 ... refreshRow() fails when primary key values
     * have escaped data in them.
     *
     * @throws Exception if an error occurs
     */
    public void testUpdatabilityWithQuotes() throws Exception {
        Statement updStmt = null;

        try {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testUpdWithQuotes");
            this.stmt.executeUpdate(
                "CREATE TABLE testUpdWithQuotes (keyField CHAR(32) PRIMARY KEY NOT NULL, field2 int)");

            PreparedStatement pStmt = this.conn.prepareStatement(
                    "INSERT INTO testUpdWithQuotes VALUES (?, ?)");
            pStmt.setString(1, "Abe's");
            pStmt.setInt(2, 1);
            pStmt.executeUpdate();

            updStmt = this.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            this.rs = updStmt.executeQuery("SELECT * FROM testUpdWithQuotes");
            this.rs.next();
            this.rs.updateInt(2, 2);
            this.rs.updateRow();
        } finally {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testUpdWithQuotes");

            if (this.rs != null) {
                this.rs.close();
            }

            this.rs = null;

            if (updStmt != null) {
                updStmt.close();
            }

            updStmt = null;
        }
    }

    /**
     * Checks whether or not ResultSet.updateClob() is implemented
     *
     * @throws Exception if the test fails
     */
    public void testUpdateClob() throws Exception {
        Statement updatableStmt = this.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);

        try {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testUpdateClob");
            this.stmt.executeUpdate(
                "CREATE TABLE testUpdateClob(intField INT NOT NULL PRIMARY KEY, clobField TEXT)");
            this.stmt.executeUpdate(
                "INSERT INTO testUpdateClob VALUES (1, 'foo')");

            this.rs = updatableStmt.executeQuery(
                    "SELECT intField, clobField FROM testUpdateClob");
            this.rs.next();

            Clob clob = this.rs.getClob(2);

            clob.setString(1, "bar");

            this.rs.updateClob(2, clob);
            this.rs.updateRow();

            this.rs.moveToInsertRow();

            clob.setString(1, "baz");
            this.rs.updateInt(1, 2);
            this.rs.updateClob(2, clob);
            this.rs.insertRow();

            clob.setString(1, "bat");
            this.rs.updateInt(1, 3);
            this.rs.updateClob(2, clob);
            this.rs.insertRow();

            this.rs.close();

            this.rs = this.stmt.executeQuery(
                    "SELECT intField, clobField FROM testUpdateClob ORDER BY intField");

            this.rs.next();
            assertTrue((this.rs.getInt(1) == 1) &&
                this.rs.getString(2).equals("bar"));

            this.rs.next();
            assertTrue((this.rs.getInt(1) == 2) &&
                this.rs.getString(2).equals("baz"));

            this.rs.next();
            assertTrue((this.rs.getInt(1) == 3) &&
                this.rs.getString(2).equals("bat"));
        } finally {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testUpdateClob");
        }
    }

    /**
     * Tests fix for  BUG#4482, ResultSet.getObject() returns wrong type
     * for strings when using prepared statements.
     *
     * @throws Exception if the test fails.
     */
    public void testBug4482() throws Exception {
        this.rs = this.conn.prepareStatement("SELECT 'abcdef'").executeQuery();
        assertTrue(this.rs.next());
        assertTrue(this.rs.getObject(1) instanceof String);
    }

    /**
     * Test fix for BUG#4689 - WasNull not getting set correctly for binary result sets.
     */
    public void testBug4689() throws Exception {
        try {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testBug4689");
            this.stmt.executeUpdate(
                "CREATE TABLE testBug4689 (tinyintField tinyint, tinyintFieldNull tinyint, " +
                "intField int, intFieldNull int, " +
                "bigintField bigint, bigintFieldNull bigint, " +
                "shortField smallint, shortFieldNull smallint, " +
                "doubleField double, doubleFieldNull double)");

            this.stmt.executeUpdate("INSERT INTO testBug4689 VALUES (1, null, " +
                "1, null, " + "1, null, " + "1, null, " + "1, null)");

            PreparedStatement pStmt = this.conn.prepareStatement(
                    "SELECT tinyintField, tinyintFieldNull," +
                    "intField, intFieldNull, " +
                    "bigintField, bigintFieldNull, " +
                    "shortField, shortFieldNull, " +
                    "doubleField, doubleFieldNull FROM testBug4689");
            this.rs = pStmt.executeQuery();
            assertTrue(this.rs.next());

            assertTrue(this.rs.getByte(1) == 1);
            assertTrue(this.rs.wasNull() == false);
            assertTrue(this.rs.getByte(2) == 0);
            assertTrue(this.rs.wasNull() == true);

            assertTrue(this.rs.getInt(3) == 1);
            assertTrue(this.rs.wasNull() == false);
            assertTrue(this.rs.getInt(4) == 0);
            assertTrue(this.rs.wasNull() == true);

            assertTrue(this.rs.getInt(5) == 1);
            assertTrue(this.rs.wasNull() == false);
            assertTrue(this.rs.getInt(6) == 0);
            assertTrue(this.rs.wasNull() == true);

            assertTrue(this.rs.getShort(7) == 1);
            assertTrue(this.rs.wasNull() == false);
            assertTrue(this.rs.getShort(8) == 0);
            assertTrue(this.rs.wasNull() == true);

            assertTrue(this.rs.getDouble(9) == 1);
            assertTrue(this.rs.wasNull() == false);
            assertTrue(this.rs.getDouble(10) == 0);
            assertTrue(this.rs.wasNull() == true);
        } finally {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testBug4689");
        }
    }

    /**
     * Tests fix for BUG#5032 -- ResultSet.getObject() doesn't return
     * type Boolean for pseudo-bit types from prepared statements on
     * 4.1.x.
     *
     * @throws Exception if the test fails.
     */
    public void testBug5032() throws Exception {
        if (versionMeetsMinimum(4, 1)) {
            PreparedStatement pStmt = null;

            try {
                this.stmt.executeUpdate("DROP TABLE IF EXISTS testBug5032");
                this.stmt.executeUpdate("CREATE TABLE testBug5032(field1 BIT)");
                this.stmt.executeUpdate("INSERT INTO testBug5032 VALUES (1)");

                pStmt = this.conn.prepareStatement(
                        "SELECT field1 FROM testBug5032");
                this.rs = pStmt.executeQuery();
                assertTrue(this.rs.next());
                assertTrue(this.rs.getObject(1) instanceof Boolean);
            } finally {
                this.stmt.executeUpdate("DROP TABLE IF EXISTS testBug5032");

                if (pStmt != null) {
                    pStmt.close();
                }
            }
        }
    }

    /**
     * Tests fix for BUG#5069 -- ResultSet.getMetaData() should not
     * return incorrectly-initialized metadata if the result set has been
     * closed, but should instead throw a SQLException. Also tests fix
     * for getRow() and getWarnings() and traversal methods.
     *
     * @throws Exception if the test fails.
     */
    public void testBug5069() throws Exception {
        try {
            this.rs = this.stmt.executeQuery("SELECT 1");
            this.rs.close();

            try {
                ResultSetMetaData md = this.rs.getMetaData();
            } catch (NullPointerException npEx) {
                fail("Should not catch NullPointerException here");
            } catch (SQLException sqlEx) {
                assertTrue(SQLError.SQL_STATE_GENERAL_ERROR.equals(
                        sqlEx.getSQLState()));
            }

            try {
                this.rs.getRow();
            } catch (NullPointerException npEx) {
                fail("Should not catch NullPointerException here");
            } catch (SQLException sqlEx) {
                assertTrue(SQLError.SQL_STATE_GENERAL_ERROR.equals(
                        sqlEx.getSQLState()));
            }

            try {
                this.rs.getWarnings();
            } catch (NullPointerException npEx) {
                fail("Should not catch NullPointerException here");
            } catch (SQLException sqlEx) {
                assertTrue(SQLError.SQL_STATE_GENERAL_ERROR.equals(
                        sqlEx.getSQLState()));
            }

            try {
                this.rs.first();
            } catch (NullPointerException npEx) {
                fail("Should not catch NullPointerException here");
            } catch (SQLException sqlEx) {
                assertTrue(SQLError.SQL_STATE_GENERAL_ERROR.equals(
                        sqlEx.getSQLState()));
            }

            try {
                this.rs.beforeFirst();
            } catch (NullPointerException npEx) {
                fail("Should not catch NullPointerException here");
            } catch (SQLException sqlEx) {
                assertTrue(SQLError.SQL_STATE_GENERAL_ERROR.equals(
                        sqlEx.getSQLState()));
            }

            try {
                this.rs.last();
            } catch (NullPointerException npEx) {
                fail("Should not catch NullPointerException here");
            } catch (SQLException sqlEx) {
                assertTrue(SQLError.SQL_STATE_GENERAL_ERROR.equals(
                        sqlEx.getSQLState()));
            }

            try {
                this.rs.afterLast();
            } catch (NullPointerException npEx) {
                fail("Should not catch NullPointerException here");
            } catch (SQLException sqlEx) {
                assertTrue(SQLError.SQL_STATE_GENERAL_ERROR.equals(
                        sqlEx.getSQLState()));
            }

            try {
                this.rs.relative(0);
            } catch (NullPointerException npEx) {
                fail("Should not catch NullPointerException here");
            } catch (SQLException sqlEx) {
                assertTrue(SQLError.SQL_STATE_GENERAL_ERROR.equals(
                        sqlEx.getSQLState()));
            }

            try {
                this.rs.next();
            } catch (NullPointerException npEx) {
                fail("Should not catch NullPointerException here");
            } catch (SQLException sqlEx) {
                assertTrue(SQLError.SQL_STATE_GENERAL_ERROR.equals(
                        sqlEx.getSQLState()));
            }

            try {
                this.rs.previous();
            } catch (NullPointerException npEx) {
                fail("Should not catch NullPointerException here");
            } catch (SQLException sqlEx) {
                assertTrue(SQLError.SQL_STATE_GENERAL_ERROR.equals(
                        sqlEx.getSQLState()));
            }

            try {
                this.rs.isBeforeFirst();
            } catch (NullPointerException npEx) {
                fail("Should not catch NullPointerException here");
            } catch (SQLException sqlEx) {
                assertTrue(SQLError.SQL_STATE_GENERAL_ERROR.equals(
                        sqlEx.getSQLState()));
            }

            try {
                this.rs.isFirst();
            } catch (NullPointerException npEx) {
                fail("Should not catch NullPointerException here");
            } catch (SQLException sqlEx) {
                assertTrue(SQLError.SQL_STATE_GENERAL_ERROR.equals(
                        sqlEx.getSQLState()));
            }

            try {
                this.rs.isAfterLast();
            } catch (NullPointerException npEx) {
                fail("Should not catch NullPointerException here");
            } catch (SQLException sqlEx) {
                assertTrue(SQLError.SQL_STATE_GENERAL_ERROR.equals(
                        sqlEx.getSQLState()));
            }

            try {
                this.rs.isLast();
            } catch (NullPointerException npEx) {
                fail("Should not catch NullPointerException here");
            } catch (SQLException sqlEx) {
                assertTrue(SQLError.SQL_STATE_GENERAL_ERROR.equals(
                        sqlEx.getSQLState()));
            }
        } finally {
            if (this.rs != null) {
                this.rs.close();
                this.rs = null;
            }
        }
    }

    /**
     * Tests for BUG#5235, ClassCastException on all-zero date field
     * when zeroDatetimeBehavior is 'convertToNull'...however it appears
     * that this bug doesn't exist. This is a placeholder until we get
     * more data from the user on how they provoke this bug to happen.
     * 
     * @throws Exception if the test fails.
     */
    public void testBug5235() throws Exception {
        try {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testBug5235");
            this.stmt.executeUpdate("CREATE TABLE testBug5235(field1 DATE)");
            this.stmt.executeUpdate(
                "INSERT INTO testBug5235 (field1) VALUES ('0000-00-00')");

            Properties props = new Properties();
            props.setProperty("zeroDateTimeBehavior", "convertToNull");

            Connection nullConn = getConnectionWithProps(props);

            this.rs = nullConn.createStatement().executeQuery("SELECT field1 FROM testBug5235");
            this.rs.next();
            assertTrue(null == this.rs.getObject(1));
        } finally {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS testBug5235");
        }
    }

    /**
     * Tests for BUG#5136, GEOMETRY types getting corrupted, turns out
     * to be a server bug.
     * 
     * @throws Exception if the test fails.
     */
    public void testBug5136() throws Exception {
    	if (false) {
        PreparedStatement toGeom = this.conn.prepareStatement(
                "select GeomFromText(?)");
        PreparedStatement toText = this.conn.prepareStatement(
                "select AsText(?)");

        String inText = "POINT(146.67596278 -36.54368233)";

        // First assert that the problem is not at the server end
        this.rs = this.stmt.executeQuery("select AsText(GeomFromText('" +
                inText + "'))");
        this.rs.next();

        String outText = this.rs.getString(1);
        this.rs.close();
        assertTrue("Server side only\n In: " + inText + "\nOut: " + outText,
            inText.equals(outText));

        // Now bring a binary geometry object to the client and send it back
        toGeom.setString(1, inText);
        this.rs = toGeom.executeQuery();
        this.rs.next();

        // Return a binary geometry object from the WKT
        Object geom = this.rs.getObject(1);
        this.rs.close();
        toText.setObject(1, geom);
        this.rs = toText.executeQuery();
        this.rs.next();

        // Return WKT from the binary geometry
        outText = this.rs.getString(1);
        this.rs.close();
        assertTrue("Server to client and back\n In: " + inText + "\nOut: " +
            outText, inText.equals(outText));
    	}
    }

	/**
	 * Tests fix for BUG#5664, ResultSet.updateByte() when on insert row
	 * throws ArrayOutOfBoundsException.
	 * 
	 * @throws Exception if the test fails.
	 */
	public void testBug5664() throws Exception {
		try {
			this.stmt.executeUpdate("DROP TABLE IF EXISTS testBug5664");
			this.stmt.executeUpdate("CREATE TABLE testBug5664 (pkfield int PRIMARY KEY NOT NULL, field1 SMALLINT)");
			this.stmt.executeUpdate("INSERT INTO testBug5664 VALUES (1, 1)");
			
			Statement updatableStmt = this.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			this.rs = updatableStmt.executeQuery("SELECT pkfield, field1 FROM testBug5664");
			this.rs.next();
			this.rs.moveToInsertRow();
			this.rs.updateInt(1, 2);
			this.rs.updateByte(2, (byte) 2);
		} finally {
			this.stmt.executeUpdate("DROP TABLE IF EXISTS testBug5664");
		}
	}
	
	public void testBogusTimestampAsString() throws Exception {
		
		this.rs = this.stmt.executeQuery("SELECT '2004-08-13 13:21:17.'");
		
		this.rs.next();
		
		// We're only checking for an exception being thrown here as the bug
		this.rs.getTimestamp(1);
		
	}
	
	/**
	 * Tests our ability to reject NaN and +/- INF in PreparedStatement.setDouble();
	 */
	public void testBug5717() throws Exception {
		try {
			this.stmt.executeUpdate("DROP TABLE IF EXISTS testBug5717");
			this.stmt.executeUpdate("CREATE TABLE testBug5717 (field1 DOUBLE)");
			this.pstmt = this.conn.prepareStatement("INSERT INTO testBug5717 VALUES (?)");
			
			try {
				this.pstmt.setDouble(1, Double.NEGATIVE_INFINITY);
				fail("Exception should've been thrown");
			} catch (Exception ex) {
				// expected
			}
			
			try {
				this.pstmt.setDouble(1, Double.POSITIVE_INFINITY);
				fail("Exception should've been thrown");
			} catch (Exception ex) {
				// expected
			}
			
			try {
				this.pstmt.setDouble(1, Double.NaN);
				fail("Exception should've been thrown");
			} catch (Exception ex) {
				// expected
			}
		} finally {
			if (this.pstmt != null) {
				this.pstmt.close();
			}
			
			this.stmt.executeUpdate("DROP TABLE IF EXISTS testBug5717");
		}
	}

	/**
	 * Tests fix for server issue that drops precision on aggregate operations on 
	 * DECIMAL types, because they come back as DOUBLEs.
	 * 
	 * @throws Exception if the test fails.
	 */
	public void testBug6537() throws Exception {
		if (versionMeetsMinimum(4, 1, 0)) {
			String tableName = "testBug6537";
			
			try {
				createTable(tableName, "(`id` int(11) NOT NULL default '0'," +
						"`value` decimal(10,2) NOT NULL default '0.00', `stringval` varchar(10)," +
						"PRIMARY KEY  (`id`)" +
						") ENGINE=MyISAM DEFAULT CHARSET=latin1");
				this.stmt.executeUpdate("INSERT INTO " + tableName + "(id, value, stringval) VALUES (1, 100.00, '100.00'), (2, 200, '200')");
				
				String sql = "SELECT SUM(value) as total FROM " + tableName + " WHERE id = ? ";
				PreparedStatement pStmt = this.conn.prepareStatement(sql);
				pStmt.setInt(1, 1);
				this.rs = pStmt.executeQuery();
				assertTrue(this.rs.next());
				
				assertTrue("100.00".equals(this.rs.getBigDecimal("total").toString()));
				
				sql = "SELECT stringval as total FROM " + tableName + " WHERE id = ? ";
				pStmt = this.conn.prepareStatement(sql);
				pStmt.setInt(1, 2);
				this.rs = pStmt.executeQuery();
				assertTrue(this.rs.next());
				
				assertTrue("200.00".equals(this.rs.getBigDecimal("total", 2).toString()));
	
			} finally {
				dropTable(tableName);
			}
		}
	}
	
    /**
     * Tests fix for BUG#6231, ResultSet.getTimestamp() on a column with TIME in it
     * fails.
     * 
     * @throws Exception if the test fails.
     */
    public void testBug6231() throws Exception {
    	try {
    		this.stmt.executeUpdate("DROP TABLE IF EXISTS testBug6231");
    		this.stmt.executeUpdate("CREATE TABLE testBug6231 (field1 TIME)");
    		this.stmt.executeUpdate("INSERT INTO testBug6231 VALUES ('09:16:00')");
    		
    		this.rs = this.stmt.executeQuery("SELECT field1 FROM testBug6231");
    		this.rs.next();
    		long asMillis = this.rs.getTimestamp(1).getTime();
    		Calendar cal = Calendar.getInstance();
    		cal.setTimeInMillis(asMillis);
    		assertTrue(cal.get(Calendar.HOUR) == 9);
    		assertTrue(cal.get(Calendar.MINUTE) == 16);
    		assertTrue(cal.get(Calendar.SECOND) == 0);
    	} finally {
    		this.stmt.executeUpdate("DROP TABLE IF EXISTS testBug6231");
    	}
    }
    
    public void testBug6619() throws Exception {
    	try {
    		this.stmt.executeUpdate("DROP TABLE IF EXISTS testBug6619");
    		this.stmt.executeUpdate("CREATE TABLE testBug6619 (field1 int)");
    		this.stmt.executeUpdate("INSERT INTO testBug6619 VALUES (1), (2)");
    		
    		PreparedStatement pStmt = this.conn.prepareStatement("SELECT SUM(field1) FROM testBug6619");
    		
    		this.rs = pStmt.executeQuery();
    		this.rs.next();
    		System.out.println(this.rs.getString(1));
    		
    	} finally {
    		this.stmt.executeUpdate("DROP TABLE IF EXISTS testBug6619");
    	}
    }
    
    /**
     * Tests for presence of BUG#6561, NPE thrown when dealing with
     * 0 dates and non-unpacked result sets.
     * 
     * @throws Exception if the test occurs.
     */
    public void testBug6561() throws Exception {
    	
    	try {
    		Properties props = new Properties();
    		props.setProperty("zeroDateTimeBehavior", "convertToNull");
    		
    		Connection zeroConn = getConnectionWithProps(props);
    		
    		this.stmt.executeUpdate ("DROP TABLE IF EXISTS testBug6561");
    		this.stmt.executeUpdate ("CREATE TABLE testBug6561 (ofield int, field1 DATE, field2 integer, field3 integer)");
    		this.stmt.executeUpdate ("INSERT INTO testBug6561 (ofield, field1,field2,field3)	VALUES (1, 0,NULL,0)");
    		this.stmt.executeUpdate ("INSERT INTO testBug6561 (ofield, field1,field2,field3) VALUES (2, '2004-11-20',NULL,0)");
    		
    		PreparedStatement ps = zeroConn.prepareStatement ("SELECT field1,field2,field3 FROM testBug6561 ORDER BY ofield");
    		this.rs = ps.executeQuery();
    		
    		assertTrue(rs.next());
    		assertTrue(null == rs.getObject("field1"));
    		assertTrue(null == rs.getObject("field2"));
    		assertTrue(0 == rs.getInt("field3"));
    		
    		assertTrue(rs.next());
    		assertTrue(rs.getString("field1").equals("2004-11-20"));
    		assertTrue(null == rs.getObject("field2"));
    		assertTrue(0 == rs.getInt("field3"));
    		
    		ps.close();
    	}
    	finally {
    		this.stmt.executeUpdate ("DROP TABLE IF EXISTS test");
    	}
    }
	
}
