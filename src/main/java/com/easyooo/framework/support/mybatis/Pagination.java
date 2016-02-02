package com.easyooo.framework.support.mybatis;

import java.util.List;

/**
 * 分页属性封装
 *
 * @author Killer
 */
public class Pagination {
	
	// constants
	static final Integer DEFAULT_LIMIT = 15;
	static final Integer DEFAULT_OFFSET = 0;
	
	// properties
	private transient Object criteria;
	private Integer limit = DEFAULT_LIMIT;
	private Integer offset = DEFAULT_OFFSET;
	private Integer totalCount;
	private List<?> records;
	
	/**
	 * 是否需要总行数，如果设置为true则触发1次count(1)的查询语句，
	 * 如果为false,不触发，默认不触发适用于手机端的查询接口
	 */
	private boolean needTotalCount = false;
	
	public Pagination(){
	}
	
	public Pagination(Object criteria){
		this.criteria = criteria;
	}
	
	public Pagination(Object criteria, Integer offset, Integer limit){
		this(criteria);
		this.limit = limit;
		this.offset = offset;
	}
	
	public Pagination(Object criteria, Integer offset, Integer limit, boolean needTotalCount){
		this(criteria, offset, limit);
		this.needTotalCount = needTotalCount;
	}
	
	public Integer getLimit() {
		return limit;
	}
	
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	
	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	@SuppressWarnings("unchecked")
	public <T> List<T> getRecords() {
		return (List<T>)records;
	}
	public void setRecords(List<?> records) {
		this.records = records;
	}

	public Object getCriteria() {
		return criteria;
	}

	public void setCriteria(Object criteria) {
		this.criteria = criteria;
	}

	public boolean isNeedTotalCount() {
		return needTotalCount;
	}

	public void setNeedTotalCount(boolean needTotalCount) {
		this.needTotalCount = needTotalCount;
	}
	
	
}
