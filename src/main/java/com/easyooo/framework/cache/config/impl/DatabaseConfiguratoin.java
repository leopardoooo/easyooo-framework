/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.config.impl;

import com.easyooo.framework.cache.ConfigurationException;
import com.easyooo.framework.cache.config.CacheBean;

/**
 * 实现从数据库读取缓存配置信息
 * 
 * @author Killer
 */
public class DatabaseConfiguratoin extends AbstractConfiguration{

	@Override
	public CacheBean parserConfiguration(Class<?> type)
			throws ConfigurationException {
		throw new UnsupportedOperationException();
	}
	
}
