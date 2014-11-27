/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.config;

/**
 * 
 * @author Killer
 */
public class KeyPropertyBean implements Comparable<KeyPropertyBean>{

	private String propertyName;
	private int order;
	
	public KeyPropertyBean(){
		super();
	}

	public KeyPropertyBean(String propertyName, int order) {
		super();
		this.propertyName = propertyName;
		this.order = order;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public int compareTo(KeyPropertyBean o) {
		return getOrder() - o.getOrder();
	}
	
}
