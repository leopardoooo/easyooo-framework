package com.easyooo.framework.support.mybatis;

import org.apache.ibatis.mapping.MappedStatement;

/**
 *代理上下文 属性封装
 * @author Killer
 */
public class InvocationContext {
	
	private MappedStatement ms;
	private Pagination pagination;
	
	public InvocationContext(){
	}
	
	public InvocationContext(MappedStatement ms, Pagination pagination){
		this.ms = ms;
		this.pagination = pagination;
	}
	
	public MappedStatement getMappedStatement() {
		return ms;
	}
	public void setMappedStatement(MappedStatement ms) {
		this.ms = ms;
	}
	public Pagination getPagination() {
		return pagination;
	}
	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}
}
