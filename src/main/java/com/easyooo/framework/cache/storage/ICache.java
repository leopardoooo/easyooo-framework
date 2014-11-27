/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.storage;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easyooo.framework.cache.CacheException;

/**
 * Cache interface definition
 * 
 * @author Killer
 */
public interface ICache {

	static Logger logger = LoggerFactory.getLogger(ICache.class);

	boolean set(String cacheKey, String value)
			throws CacheException;

	boolean mod(String cacheKey, String value)
			throws CacheException;

	Long del(String cacheKey) throws CacheException;

	String get(String cacheKey) throws CacheException;

	List<String> gets(String... cacheKey) throws CacheException;
	
	boolean sets(String... keyvalues) throws CacheException;

	Long addMembers(String groupKey, String... entityCacheKeys)
			throws CacheException;

	Long delMembers(String groupKey, String... entityCacheKeys)
			throws CacheException;

	Set<String> getMembers(String groupKey) throws CacheException;

	/**
	 * Gets the current cache object length
	 * 
	 * @return
	 */
	int getSize();

}
