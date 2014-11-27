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
}
