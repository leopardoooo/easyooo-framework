/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.config;

import com.easyooo.framework.cache.CacheLevel;
import com.easyooo.framework.cache.KeyBuilder;

/**
 * 
 * @author Killer
 */
public class Basic {
	
	private Class<?> xbeanClass;
	
	private String prefix;
	
	private Class<? extends KeyBuilder> keyBuilder;
	
	private CacheLevel level;
	
	public Basic() {
		super();
	}

	public Basic(Class<?> xbean, String prefix,
			Class<? extends KeyBuilder> keyBuilder, CacheLevel level) {
		super();
		this.xbeanClass = xbean;
		this.prefix = prefix;
		this.keyBuilder = keyBuilder;
		this.level = level;
	}

	public Class<?> getXbeanClass() {
		return xbeanClass;
	}

	public void setXbeanClass(Class<?> xbeanClass) {
		this.xbeanClass = xbeanClass;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Class<? extends KeyBuilder> getKeyBuilder() {
		return keyBuilder;
	}

	public void setKeyBuilder(Class<? extends KeyBuilder> keyBuilder) {
		this.keyBuilder = keyBuilder;
	}

	public CacheLevel getLevel() {
		return level;
	}

	public void setLevel(CacheLevel level) {
		this.level = level;
	}
}
