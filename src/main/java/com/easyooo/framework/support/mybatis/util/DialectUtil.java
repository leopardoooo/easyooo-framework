package com.easyooo.framework.support.mybatis.util;

import com.easyooo.framework.support.mybatis.DBMS;
import com.easyooo.framework.support.mybatis.Dialect;
import com.easyooo.framework.support.mybatis.impl.MySQLDialect;

/**
 *
 * @author Killer
 */
public class DialectUtil {

	public Dialect switchDialect(String dbmsString)throws Exception{
		DBMS dbms = null;
		try{
			dbms = DBMS.valueOf(dbmsString);
		}catch(Exception e){
			throw e;
		}
		if(dbms == DBMS.MYSQL){
			return new MySQLDialect();
		}
		throw new UnsupportedOperationException("The database is not supported.");
	}
	
}
