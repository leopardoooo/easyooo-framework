/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.config;

import java.util.List;
import java.util.Map;

import com.easyooo.framework.cache.CacheLevel;
import com.easyooo.framework.cache.KeyBuilder;

/**
 * 
 * @author Killer
 */
public class CacheBean extends Basic{

	private Map<Class<?>, MergingBean> mergings;
	
	private List<KeyPropertyBean> keyPropertyList;
	
	private Map<String, List<GroupBean>> groups;
	
	private List<ModelField> fields;
	
	private boolean miniTable = Boolean.FALSE;
	
	public CacheBean(){
		super();
	}
	
	public CacheBean(Class<?> xbean, String prefix,
			Class<? extends KeyBuilder> keyBuilder, CacheLevel level, boolean miniTable) {
		super(xbean, prefix, keyBuilder, level);
		this.miniTable = miniTable;
	}

	public Map<Class<?>, MergingBean> getMergings() {
		return mergings;
	}

	public void setMergings(Map<Class<?>, MergingBean> mergings) {
		this.mergings = mergings;
	}

	public List<KeyPropertyBean> getKeyPropertyList() {
		return keyPropertyList;
	}

	public void setKeyPropertyList(List<KeyPropertyBean> keyPropertyList) {
		this.keyPropertyList = keyPropertyList;
	}

	public Map<String, List<GroupBean>> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, List<GroupBean>> groups) {
		this.groups = groups;
	}

	public List<ModelField> getFields() {
		return fields;
	}

	public void setFields(List<ModelField> fields) {
		this.fields = fields;
	}

	public boolean isMiniTable() {
		return miniTable;
	}

	public void setMiniTable(boolean miniTable) {
		this.miniTable = miniTable;
	}
}
