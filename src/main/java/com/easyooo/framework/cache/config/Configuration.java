/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.config;

import com.easyooo.framework.cache.ConfigurationException;

/**
 * 缓存配置接口定义
 * @author Killer
 */
public interface Configuration {
	
	public static final String DEFAULT_GROUP_NAME = "default";
	
	public static final String DEFAULT_MINI_TABLE = "__mini_";
	
	/**
	 * 
	 * 解析实体Class对应的缓存配置，并需要验证配置的合法性
	 * 
	 * @see #validate(CacheBean)
	 * 
	 * @return
	 * @throws ConfigurationException
	 */
	public CacheBean parserConfiguration(
			Class<?> type)
			throws ConfigurationException;
	

	/**
	 * 获取一个缓存配置信息
	 * 
	 * @param type 实体类的Class
	 * @throws ConfigurationException
	 */
	public CacheBean get(final Class<?> type)
			throws ConfigurationException;
	
}
