package com.easyooo.framework.support.mybatis;

/**
 *SQL方言接口
 * 
 * @author Killer
 */
public interface Dialect {
	
	String getPagingSQL(String sql);
	
	
	String getCountingSQL(String sql);
	
	
	Order[] order();
}
