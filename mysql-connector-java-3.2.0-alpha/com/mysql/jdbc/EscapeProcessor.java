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

/**
 * EscapeProcessor performs all escape code processing as outlined in the JDBC
 * spec by JavaSoft.
 */
package com.mysql.jdbc;

import java.sql.SQLException;

import java.util.Locale;
import java.util.StringTokenizer;


class EscapeProcessor {
	
    /**
     * Escape process one string
     *
     * @param sql the SQL to escape process.
     *
     * @return the SQL after it has been escape processed.
     *
     * @throws java.sql.SQLException DOCUMENT ME!
     * @throws SQLException DOCUMENT ME!
     */
    public static final Object escapeSQL(String sql)
        throws java.sql.SQLException {
        boolean replaceEscapeSequence = false;
        String escapeSequence = null;
        
        if (sql == null) {
            return null;
        }

        /*
         * Short circuit this code if we don't have a matching pair of
         * "{}". - Suggested by Ryan Gustafason
         */
        int beginBrace = sql.indexOf('{');
        int nextEndBrace = (beginBrace == -1) ? (-1)
                                              : sql.indexOf('}', beginBrace);

        if (nextEndBrace == -1) {
        	return sql;
        }

        StringBuffer newSql = new StringBuffer();

        EscapeTokenizer escapeTokenizer = new EscapeTokenizer(sql);
        
        byte usesVariables = Statement.USES_VARIABLES_FALSE;
        
        while (escapeTokenizer.hasMoreTokens()) {
            String token = escapeTokenizer.nextToken();

            if (token.length() != 0) {
                if (token.charAt(0) == '{') { // It's an escape code

                    if (!token.endsWith("}")) {
                        throw new SQLException("Not a valid escape sequence: "
                            + token);
                    }

                    if (token.length() > 2) {
                        int nestedBrace = token.indexOf('{', 2);

                        if (nestedBrace != -1) {
                            StringBuffer buf = new StringBuffer(token.substring(
                                        0, 1));

                            Object remainingResults =  escapeSQL(token.substring(1,
							token.length() - 1));
                            
                            String remaining = null;
                            
                            if (remainingResults instanceof String) {
                            	remaining = (String)remainingResults;
                            } else {
                            	remaining = ((EscapeProcessorResult)remainingResults).escapedSql;
                            
                            	if (usesVariables != Statement.USES_VARIABLES_TRUE) {
                            		usesVariables = ((EscapeProcessorResult)remainingResults).usesVariables;
                            	}
                            }

                            buf.append(remaining);

                            buf.append('}');

                            token = buf.toString();
                        }
                    }

                    // nested escape code
                    // Compare to tokens with _no_ whitespace
                    String collapsedToken = removeWhitespace(token);

                    /*
                     * Process the escape code
                     */
                    if (StringUtils.startsWithIgnoreCase(collapsedToken,
                                "{escape")) {
                        try {
                            StringTokenizer st = new StringTokenizer(token, " '");
                            st.nextToken(); // eat the "escape" token
                            escapeSequence = st.nextToken();

                            if (escapeSequence.length() < 3) {
                                throw new SQLException(
                                    "Syntax error for escape sequence '"
                                    + token + "'", "42000");
                            }

                            escapeSequence = escapeSequence.substring(1,
                                    escapeSequence.length() - 1);
                            replaceEscapeSequence = true;
                        } catch (java.util.NoSuchElementException e) {
                            throw new SQLException(
                                "Syntax error for escape sequence '" + token
                                + "'", "42000");
                        }
                    } else if (StringUtils.startsWithIgnoreCase(
                                collapsedToken, "{fn")) {
                        // just pass functions right to the DB
                        int startPos = StringUtils.indexOfIgnoreCase(token, "fn ") + 3;
                        int endPos = token.length() - 1; // no }
                        newSql.append(token.substring(startPos, endPos));
                    } else if (StringUtils.startsWithIgnoreCase(
                                collapsedToken, "{d")) {
                        int startPos = token.indexOf('\'') + 1;
                        int endPos = token.lastIndexOf('\''); // no }

                        if ((startPos == -1) || (endPos == -1)) {
                            throw new SQLException(
                                "Syntax error for DATE escape sequence '"
                                + token + "'", "42000");
                        }

                        String argument = token.substring(startPos, endPos);

                        try {
                            StringTokenizer st = new StringTokenizer(argument,
                                    " -");
                            String year4 = st.nextToken();
                            String month2 = st.nextToken();
                            String day2 = st.nextToken();
                            String dateString = "'" + year4 + "-" + month2
                                + "-" + day2 + "'";
                            newSql.append(dateString);
                        } catch (java.util.NoSuchElementException e) {
                            throw new SQLException(
                                "Syntax error for DATE escape sequence '"
                                + argument + "'", "42000");
                        }
                    } else if (StringUtils.startsWithIgnoreCase(
                                collapsedToken, "{ts")) {
                        int startPos = token.indexOf('\'') + 1;
                        int endPos = token.lastIndexOf('\''); // no }

                        if ((startPos == -1) || (endPos == -1)) {
                            throw new SQLException(
                                "Syntax error for TIMESTAMP escape sequence '"
                                + token + "'", "42000");
                        }

                        String argument = token.substring(startPos, endPos);

                        try {
                            StringTokenizer st = new StringTokenizer(argument,
                                    " .-:");
                            String year4 = st.nextToken();
                            String month2 = st.nextToken();
                            String day2 = st.nextToken();
                            String hour = st.nextToken();
                            String minute = st.nextToken();
                            String second = st.nextToken();

                            /*
                             * For now, we get the fractional seconds
                             * part, but we don't use it, as MySQL doesn't
                             * support it in it's TIMESTAMP data type
                             *
                            String fractionalSecond = "";

                            if (st.hasMoreTokens()) {
                                fractionalSecond = st.nextToken();
                            }
                            */
                            /*
                             * Use the full format because number format
                             * will not work for "between" clauses.
                             *
                             * Ref. Mysql Docs
                             *
                             * You can specify DATETIME, DATE and TIMESTAMP values
                             * using any of a common set of formats:
                             *
                             * As a string in either 'YYYY-MM-DD HH:MM:SS' or
                             * 'YY-MM-DD HH:MM:SS' format.
                             *
                             * Thanks to Craig Longman for pointing out this bug
                             */
                            newSql.append("'").append(year4).append("-")
                                  .append(month2).append("-").append(day2)
                                  .append(" ").append(hour).append(":")
                                  .append(minute).append(":").append(second)
                                  .append("'");
                        } catch (java.util.NoSuchElementException e) {
                            throw new SQLException(
                                "Syntax error for TIMESTAMP escape sequence '"
                                + argument + "'", "42000");
                        }
                    } else if (StringUtils.startsWithIgnoreCase(
                                collapsedToken, "{t")) {
                        int startPos = token.indexOf('\'') + 1;
                        int endPos = token.lastIndexOf('\''); // no }

                        if ((startPos == -1) || (endPos == -1)) {
                            throw new SQLException(
                                "Syntax error for TIME escape sequence '"
                                + token + "'", "42000");
                        }

                        String argument = token.substring(startPos, endPos);

                        try {
                            StringTokenizer st = new StringTokenizer(argument,
                                    " :");
                            String hour = st.nextToken();
                            String minute = st.nextToken();
                            String second = st.nextToken();
                            String timeString = "'" + hour + ":" + minute + ":"
                                + second + "'";
                            newSql.append(timeString);
                        } catch (java.util.NoSuchElementException e) {
                            throw new SQLException(
                                "Syntax error for escape sequence '" + argument
                                + "'", "42000");
                        }
                    } else if (StringUtils.startsWithIgnoreCase(
                                collapsedToken, "{call")
                            || StringUtils.startsWithIgnoreCase(
                                collapsedToken, "{?=call")) {
                       
                        int startPos = StringUtils.indexOfIgnoreCase(token, "CALL") + 5;
                        int endPos = token.length() - 1;

                        newSql.append("CALL ");
                        newSql.append(token.substring(startPos, endPos));
                    } else if (StringUtils.startsWithIgnoreCase(
                                collapsedToken, "{oj")) {
                        // MySQL already handles this escape sequence
                        // because of ODBC. Cool.
                        newSql.append(token);
                    }
                } else {
                    newSql.append(token); // it's just part of the query
                }
            }
        }

        String escapedSql = newSql.toString();

        //
        // FIXME: Let MySQL do this, however requires
        //        lightweight parsing of statement
        //
        if (replaceEscapeSequence) {
            String currentSql = escapedSql;

            while (currentSql.indexOf(escapeSequence) != -1) {
                int escapePos = currentSql.indexOf(escapeSequence);
                String lhs = currentSql.substring(0, escapePos);
                String rhs = currentSql.substring(escapePos + 1,
                        currentSql.length());
                currentSql = lhs + "\\" + rhs;
            }

            escapedSql = currentSql;
        }

        EscapeProcessorResult epr = new EscapeProcessorResult();
        epr.escapedSql = escapedSql;
        
        if (usesVariables != Statement.USES_VARIABLES_TRUE) {
        	if (escapeTokenizer.sawVariableUse()) {
        		epr.usesVariables = Statement.USES_VARIABLES_TRUE;
        	} else {
        		epr.usesVariables = Statement.USES_VARIABLES_FALSE;
        	}
        }
        
        return epr;
    }

    /**
     * Removes all whitespace from the given String. We use this to make escape
     * token comparison white-space ignorant.
     *
     * @param toCollapse the string to remove the whitespace from
     *
     * @return a string with _no_ whitespace.
     */
    private static String removeWhitespace(String toCollapse) {
        if (toCollapse == null) {
            return null;
        }

        int length = toCollapse.length();

        StringBuffer collapsed = new StringBuffer(length);

        for (int i = 0; i < length; i++) {
            char c = toCollapse.charAt(i);

            if (!Character.isWhitespace(c)) {
                collapsed.append(c);
            }
        }

        return collapsed.toString();
    }
}
