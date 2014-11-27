package com.easyooo.framework.sharding;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 数据源信息封装
 *
 * @author Killer
 */
@SuppressWarnings("serial")
public class DataSourceInfo implements Serializable{

	private String dataSourceKey;
	private String dataSourceProviderClassName;
	private Map<String, String> propMap;
	private Date createDate;
	
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getDataSourceKey() {
		return dataSourceKey;
	}
	public void setDataSourceKey(String dataSourceKey) {
		this.dataSourceKey = dataSourceKey;
	}
	public String getDataSourceProviderClassName() {
		return dataSourceProviderClassName;
	}
	public void setDataSourceProviderClassName(String dataSourceProviderClassName) {
		this.dataSourceProviderClassName = dataSourceProviderClassName;
	}
	public Map<String, String> getPropMap() {
		return propMap;
	}
	public void setPropMap(Map<String, String> propMap) {
		this.propMap = propMap;
	}
	
}
