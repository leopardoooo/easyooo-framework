package com.easyooo.framework.support.mybatis.impl;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.easyooo.framework.support.mybatis.Dialect;
import com.easyooo.framework.support.mybatis.Order;

/**
 * MySQL方言
 * 
 * @author Killer
 */
public class MySQLDialect implements Dialect {

	final String PAGING_SQL_TPL = "{0} LIMIT ?,?";

	final String COUNTING_SQL_TPL = "SELECT COUNT(1) FROM ";
	final Pattern COUNTING_PATTERN = Pattern.compile("select[\\s\\S]*?[\\s]from[\\s]", Pattern.CASE_INSENSITIVE);

	final String COUNTING_SUB_SQL_TPL = "SELECT COUNT(1) FROM ({0}) T";
	static final Pattern COUNT_SUB_PATTERN = Pattern.compile("(\\s+group\\s+by)|(\\s+union\\s+)", Pattern.CASE_INSENSITIVE);
	
	@Override
	public String getPagingSQL(String sql) {
		return MessageFormat.format(PAGING_SQL_TPL, sql);
	}

	@Override
	public String getCountingSQL(String sql) {
		if(COUNT_SUB_PATTERN.matcher(sql).find()){
			return String.format(COUNTING_SUB_SQL_TPL, sql);
		}else{
			StringBuffer buffer = new StringBuffer();
			Matcher matcher = COUNTING_PATTERN.matcher(sql);
			if(matcher.find()){
				matcher.appendReplacement(buffer, COUNTING_SQL_TPL);
			}
			matcher.appendTail( buffer);
			return buffer.toString();
		}
	}
	
	@Override
	public Order[] order() {
		return new Order[]{Order.OFFSET, Order.LIMIT};
	}
}
