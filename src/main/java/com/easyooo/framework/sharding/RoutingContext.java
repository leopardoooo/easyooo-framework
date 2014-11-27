package com.easyooo.framework.sharding;

import lombok.ToString;


/**
 * 路由上下文定义
 *
 * @author Killer
 */
@ToString
public class RoutingContext {

	private Object target;
	private String table;
	private Long keyword;
	
	public Object getTarget() {
		return target;
	}
	public void setTarget(Object target) {
		this.target = target;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public Long getKeyword() {
		return keyword;
	}
	public void setKeyword(Long keyword) {
		this.keyword = keyword;
	}
	
}
