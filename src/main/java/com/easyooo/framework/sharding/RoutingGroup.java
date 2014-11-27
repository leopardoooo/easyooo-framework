package com.easyooo.framework.sharding;

import java.io.Serializable;
import java.util.List;

import lombok.ToString;

/**
 * 模块对应的节点配置，同模块可配置多条
 *
 * @author Killer
 */
@SuppressWarnings("serial")
@ToString
public class RoutingGroup implements Serializable{
	
	private static final Long DEFAULT_START_ID = 0L;
	
	private Module module;
	private List<DataSourceKey> dataSourceKeys;
	private Long startId = DEFAULT_START_ID;
	private NumberRange range;
	
	private RoutingRule routingRule;
	
	
	public Module getModule() {
		return module;
	}
	
	public void setModule(Module module) {
		this.module = module;
	}
	
	public List<DataSourceKey> getDataSourceKeys() {
		return dataSourceKeys;
	}
	
	public void setDataSourceKeys(List<DataSourceKey> dataSourceKeys) {
		this.dataSourceKeys = dataSourceKeys;
	}
	
	public Long getStartId() {
		return startId;
	}

	public void setStartId(Long startId) {
		this.startId = startId;
	}
	
	public NumberRange getRange() {
		return range;
	}

	public void setRange(NumberRange range) {
		this.range = range;
	}

	public RoutingRule getRoutingRule() {
		return routingRule;
	}

	public void setRoutingRule(RoutingRule routingRule) {
		this.routingRule = routingRule;
	}

	public RoutingGroup(){
	}
	
	public RoutingGroup(Module module, List<DataSourceKey> dataSourceKeys,
			Long startId) {
		super();
		this.module = module;
		this.dataSourceKeys = dataSourceKeys;
		this.startId = startId;
	}
}
