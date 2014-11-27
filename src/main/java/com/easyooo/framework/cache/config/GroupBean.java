/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.config;

import com.easyooo.framework.common.util.GroupingKey;

/**
 * 
 * @author Killer
 */
public class GroupBean extends KeyPropertyBean implements GroupingKey<String>{
	
	private String name;
	
	private boolean userDefined = true;
	
	public GroupBean(){
		super();
	}

	public GroupBean(String groupName, String propertyName, int order) {
		super(propertyName, order);
		this.name = groupName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getGroupingKey() {
		return name;
	}

	public boolean isUserDefined() {
		return userDefined;
	}

	public void setUserDefined(boolean userDefined) {
		this.userDefined = userDefined;
	}
}
