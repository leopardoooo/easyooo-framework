/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.storage;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.easyooo.framework.cache.CacheException;

/**
 * 
 * @author Killer
 */
public class LoggingCache implements ICache{

	final Logger logger = LoggerFactory.getLogger(getClass());
	private ICache delegate;
	protected AtomicInteger requests = new AtomicInteger(0);
	protected AtomicInteger hits = new AtomicInteger(0);
	
	public LoggingCache(ICache delegate){
		this.delegate = delegate;
	}
	
	@Override
	public boolean set(String cacheKey, String value)
			throws CacheException{
		if(logger.isInfoEnabled()){
			logger.info("Add an object to the cache [" + cacheKey + "]");
		}
		boolean success = delegate.set(cacheKey, value);
		if(!success){
			logger.warn("Set the cache["+ cacheKey +"] failure.");
		}
		return success;
	}

	@Override
	public List<String> gets(String...cacheKey) throws CacheException{
		for (int i = 0; i < cacheKey.length; i++) {
			requests.incrementAndGet();
		}
		List<String> o = this.delegate.gets(cacheKey);
		for (String string : o) {
			if(null != string && !"".equals(string)){
				hits.incrementAndGet();
			}
		}
		if(logger.isDebugEnabled()){
			logger.debug("Cache Hit Ratio: " + getHitRatio());
		}
		return o;
	}
	
	@Override
	public boolean sets(String... keyvalues) throws CacheException {
		if(logger.isDebugEnabled()){
			logger.info("Add objects to the cache " + Arrays.toString(keyvalues));
		}
		
		boolean success = this.delegate.sets(keyvalues);
		if(!success){
			logger.warn("Set the cache failure.");
		}
		return success;
	}

	@Override
	public boolean mod(String cacheKey, String value)
			throws CacheException {
		if(logger.isInfoEnabled()){
			logger.info("Mod an object to the cache [" + cacheKey + "]");
		}
		return delegate.mod(cacheKey, value);
	}

	@Override
	public Long del(String cacheKey) throws CacheException {
		Long rows = delegate.del(cacheKey);
		if(logger.isInfoEnabled()){
			logger.info("Removed the cache["+ cacheKey +"]");
		}
		return rows;
	}

	@Override
	public String get(String cacheKey) throws CacheException {
		requests.incrementAndGet();
		String o = this.delegate.get(cacheKey);
		if(o != null){
			hits.incrementAndGet();
		}
		if(logger.isDebugEnabled()){
			logger.debug("Cache["+ cacheKey +"] Hit Ratio: " + getHitRatio());
		}
		return o;
	}

	@Override
	public Long addMembers(String groupKey, String... entityCacheKeys)
			throws CacheException {
		if(logger.isInfoEnabled()){
			logger.info("Add elements[" + StringUtils.arrayToDelimitedString(entityCacheKeys, ",") + "] to " + groupKey);
		}
		return delegate.addMembers(groupKey, entityCacheKeys);
	}

	@Override
	public Long delMembers(String groupKey, String... entityCacheKeys)
			throws CacheException {
		if(logger.isInfoEnabled()){
			logger.info("Del members[" + StringUtils.arrayToDelimitedString(entityCacheKeys, ",") + "] from " + groupKey);
		}
		return delegate.delMembers(groupKey, entityCacheKeys);
	}

	@Override
	public Set<String> getMembers(String groupKey) throws CacheException {
		return delegate.getMembers(groupKey);
	}

	@Override
	public int getSize() {
		return delegate.getSize();
	}
	
	private double getHitRatio() {
		return (double) hits.intValue() / (double) requests.intValue();
	}
}
