/*
    Copyright (C) 2004 MySQL AB

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


class BindValue {
	
	
	
    Object value; /* The value to store */
    boolean isLongData; /* long data indicator */
    boolean isNull; /* NULL indicator */
    int bufferType; /* buffer type */
    long bindLength; /* Default length of data */
    byte byteBinding;
    short shortBinding;
    int intBinding;
    long longBinding;
    double doubleBinding;
	float floatBinding;
    boolean isSet = false; /* has this parameter been set? */

    BindValue() {
    }

    void reset() {
    	this.isSet = false;
    	this.value = null;
    	this.isLongData = false;
    	
    	this.byteBinding = 0;
    	this.shortBinding = 0;
    	this.intBinding = 0;
    	this.longBinding = 0L;
    	this.floatBinding = 0;
    	this.doubleBinding = 0D;
    }
    
    BindValue(BindValue copyMe) {
    	this.value = copyMe.value;
    	this.isSet = copyMe.isSet;
        this.isLongData = copyMe.isLongData;
        this.isNull = copyMe.isNull;
        this.bufferType = copyMe.bufferType;
        this.bindLength = copyMe.bindLength;
        this.byteBinding = copyMe.byteBinding;
        this.shortBinding = copyMe.shortBinding;
        this.intBinding = copyMe.intBinding;
        this.longBinding = copyMe.longBinding;
        this.floatBinding = copyMe.floatBinding;
        this.doubleBinding = copyMe.doubleBinding;
    }
}