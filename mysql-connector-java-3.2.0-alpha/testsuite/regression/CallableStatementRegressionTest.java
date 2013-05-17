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

import testsuite.BaseTestCase;


/**
 * Tests fixes for bugs in CallableStatement code.
 *
 * @version $Id: CallableStatementRegressionTest.java,v 1.1.2.2.2.5 2004/12/13 22:22:03 mmatthew Exp $
 */
public class CallableStatementRegressionTest extends BaseTestCase {
    /**
     * DOCUMENT ME!
     *
     * @param name
     */
    public CallableStatementRegressionTest(String name) {
        super(name);

        // TODO Auto-generated constructor stub
    }

    /**
     * Runs all test cases in this test suite
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(CallableStatementRegressionTest.class);
    }

    /**
     * Tests fix for BUG#3539 getProcedures() does not return any procedures in
     * result set
     *
     * @throws Exception if an error occurs.
     */
    public void testBug3539() throws Exception {
        if (versionMeetsMinimum(5, 0)) {
            try {
                this.stmt.executeUpdate("DROP PROCEDURE IF EXISTS testBug3539");
                this.stmt.executeUpdate("CREATE PROCEDURE testBug3539()\n"
                    + "BEGIN\n" + "SELECT 1;" + "end\n");

                this.rs = this.conn.getMetaData().getProcedures(null, null,
                        "testBug3539");

                assertTrue(this.rs.next());
                assertTrue("testBug3539".equals(this.rs.getString(3)));
            } finally {
                this.stmt.executeUpdate("DROP PROCEDURE IF EXISTS testBug3539");
            }
        }
    }

    /**
     * Tests fix for BUG#3540 getProcedureColumns doesn't work with wildcards
     * for procedure name
     *
     * @throws Exception if an error occurs.
     */
    public void testBug3540() throws Exception {
        if (versionMeetsMinimum(5, 0)) {
            try {
                this.stmt.executeUpdate("DROP PROCEDURE IF EXISTS testBug3540");
                this.stmt.executeUpdate(
                    "CREATE PROCEDURE testBug3540(x int, out y int)\n"
                    + "BEGIN\n" + "SELECT 1;" + "end\n");

                this.rs = this.conn.getMetaData().getProcedureColumns(null,
                        null, "testBug3540%", "%");

                assertTrue(this.rs.next());
                assertTrue("testBug3540".equals(this.rs.getString(3)));
                assertTrue("x".equals(this.rs.getString(4)));

                assertTrue(this.rs.next());
                assertTrue("testBug3540".equals(this.rs.getString(3)));
                assertTrue("y".equals(this.rs.getString(4)));

                assertTrue(!this.rs.next());
            } finally {
                this.stmt.executeUpdate("DROP PROCEDURE IF EXISTS testBug3540");
            }
        }
    }
}
