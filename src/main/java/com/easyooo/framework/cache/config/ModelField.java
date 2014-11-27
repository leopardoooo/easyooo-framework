/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.config;

/**
 * 
 * @author Killer
 */
public class ModelField {

	private String propertyName;
	
	public ModelField(String propertyName) {
		super();
		this.propertyName = propertyName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	
}
