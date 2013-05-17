/*
 * Created on Nov 9, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.Locale;

/**
 * @author mmatthew
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
class TypeDescriptor {

	int bufferLength;
	int charOctetLength;
	int columnSize;
	short dataType;
	int decimalDigits;
	String isNullable;
	int nullability;
	int numPrecRadix = 10;
	String typeName;
	Connection conn;
	
	TypeDescriptor(Connection conn, String typeInfo, String nullabilityInfo)
	    throws SQLException {
		this.conn = conn;
		
		String mysqlType = "";
	    String fullMysqlType = null;
	
	    if (typeInfo.indexOf("(") != -1) {
	        mysqlType = typeInfo.substring(0, typeInfo.indexOf("("));
	    } else {
	        mysqlType = typeInfo;
	    }
	    
	    int indexOfUnsignedInMysqlType = StringUtils.indexOfIgnoreCase(mysqlType, "unsigned");
	
	    if (indexOfUnsignedInMysqlType != -1) {
	    	mysqlType = mysqlType.substring(0, (indexOfUnsignedInMysqlType - 1));
	    }
	    
	    // Add unsigned to typename reported to enduser as 'native type', if present
	    
	    if (StringUtils.indexOfIgnoreCase(typeInfo, "unsigned") != -1) {
	    	fullMysqlType = mysqlType + " unsigned";
	    } else {
	    	fullMysqlType = mysqlType;
	    }
	
	    if (this.conn.getCapitalizeTypeNames()) {
	    	fullMysqlType = fullMysqlType.toUpperCase(Locale.ENGLISH);
	    }
	    
	    this.dataType = (short) MysqlDefs.mysqlToJavaType(mysqlType);
	
	    this.typeName = fullMysqlType;
	
	    // Figure Out the Size
	    if (typeInfo != null) {
	        if (StringUtils.startsWithIgnoreCase(typeInfo, "enum") ||
	                StringUtils.startsWithIgnoreCase(typeInfo, "set")) {
	            String temp = typeInfo.substring(typeInfo.indexOf("("),
	                    typeInfo.lastIndexOf(")"));
	            java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(temp,
	                    ",");
	            int maxLength = 0;
	
	            while (tokenizer.hasMoreTokens()) {
	                maxLength = Math.max(maxLength,
	                        (tokenizer.nextToken().length() - 2));
	            }
	
	            this.columnSize = maxLength;
	            this.decimalDigits = 0;
	        } else if (typeInfo.indexOf(",") != -1) {
	            // Numeric with decimals
	            this.columnSize = Integer.parseInt(typeInfo.substring((typeInfo.indexOf(
	                            "(") + 1), (typeInfo.indexOf(","))));
	            this.decimalDigits = Integer.parseInt(typeInfo.substring((typeInfo.indexOf(
	                            ",") + 1), (typeInfo.indexOf(")"))));
	        } else {
	            this.columnSize = 0;
	
	            /* If the size is specified with the DDL, use that */
	            if (typeInfo.indexOf("(") != -1) {
	                int endParenIndex = typeInfo.indexOf(")");
	
	                if (endParenIndex == -1) {
	                    endParenIndex = typeInfo.length();
	                }
	
	                this.columnSize = Integer.parseInt(typeInfo.substring((typeInfo.indexOf(
	                                "(") + 1), endParenIndex));
	            } else if (typeInfo.equalsIgnoreCase("tinyint")) {
	                this.columnSize = 1;
	            } else if (typeInfo.equalsIgnoreCase("smallint")) {
	                this.columnSize = 6;
	            } else if (typeInfo.equalsIgnoreCase("mediumint")) {
	                this.columnSize = 6;
	            } else if (typeInfo.equalsIgnoreCase("int")) {
	                this.columnSize = 11;
	            } else if (typeInfo.equalsIgnoreCase("integer")) {
	                this.columnSize = 11;
	            } else if (typeInfo.equalsIgnoreCase("bigint")) {
	                this.columnSize = 25;
	            } else if (typeInfo.equalsIgnoreCase("int24")) {
	                this.columnSize = 25;
	            } else if (typeInfo.equalsIgnoreCase("real")) {
	                this.columnSize = 12;
	            } else if (typeInfo.equalsIgnoreCase("float")) {
	                this.columnSize = 12;
	            } else if (typeInfo.equalsIgnoreCase("decimal")) {
	                this.columnSize = 12;
	            } else if (typeInfo.equalsIgnoreCase("numeric")) {
	                this.columnSize = 12;
	            } else if (typeInfo.equalsIgnoreCase("double")) {
	                this.columnSize = 22;
	            } else if (typeInfo.equalsIgnoreCase("char")) {
	                this.columnSize = 1;
	            } else if (typeInfo.equalsIgnoreCase("varchar")) {
	                this.columnSize = 255;
	            } else if (typeInfo.equalsIgnoreCase("date")) {
	                this.columnSize = 10;
	            } else if (typeInfo.equalsIgnoreCase("time")) {
	                this.columnSize = 8;
	            } else if (typeInfo.equalsIgnoreCase("timestamp")) {
	                this.columnSize = 19;
	            } else if (typeInfo.equalsIgnoreCase("datetime")) {
	                this.columnSize = 19;
	            } else if (typeInfo.equalsIgnoreCase("tinyblob")) {
	                this.columnSize = 255;
	            } else if (typeInfo.equalsIgnoreCase("blob")) {
	                this.columnSize = 65535;
	            } else if (typeInfo.equalsIgnoreCase("mediumblob")) {
	                this.columnSize = 16277215;
	            } else if (typeInfo.equalsIgnoreCase("longblob")) {
	                this.columnSize = Integer.MAX_VALUE;
	            } else if (typeInfo.equalsIgnoreCase("tinytext")) {
	                this.columnSize = 255;
	            } else if (typeInfo.equalsIgnoreCase("text")) {
	                this.columnSize = 65535;
	            } else if (typeInfo.equalsIgnoreCase("mediumtext")) {
	                this.columnSize = 16277215;
	            } else if (typeInfo.equalsIgnoreCase("longtext")) {
	                this.columnSize = Integer.MAX_VALUE;
	            } else if (typeInfo.equalsIgnoreCase("enum")) {
	                this.columnSize = 255;
	            } else if (typeInfo.equalsIgnoreCase("set")) {
	                this.columnSize = 255;
	            }
	
	            this.decimalDigits = 0;
	        }
	    } else {
	        this.decimalDigits = 0;
	        this.columnSize = 0;
	    }
	
	    //		BUFFER_LENGTH
	    this.bufferLength = MysqlIO.getMaxBuf();
	
	    //		NUM_PREC_RADIX (is this right for char?)
	    this.numPrecRadix = 10;
	
	    // Nullable?
	    if (nullabilityInfo != null) {
	        if (nullabilityInfo.equals("YES")) {
	            this.nullability = java.sql.DatabaseMetaData.columnNullable;
	            this.isNullable = "YES";
	
	            // IS_NULLABLE
	        } else {
	            this.nullability = java.sql.DatabaseMetaData.columnNoNulls;
	            this.isNullable = "NO";
	        }
	    } else {
	        this.nullability = java.sql.DatabaseMetaData.columnNoNulls;
	        this.isNullable = "NO";
	    }
	}

}
