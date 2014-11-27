/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.storage;

import java.util.List;
import java.util.Set;

import com.easyooo.framework.cache.CacheException;

/**
 * 
 * @author Killer
 */
public class CheckParameterCache implements ICache{
	private ICache delegate;
	
	public CheckParameterCache(ICache delegate){
		this.delegate = delegate;
	}
	
	public boolean set(String cacheKey, String value)
			throws CacheException{
		checkKey(cacheKey);
		checkValue(value);
		return delegate.set(cacheKey, value);
	}
	
	@Override
	public boolean sets(String... keyvalues) throws CacheException {
		if(keyvalues == null){
			return false;
		}
		if(keyvalues.length % 2 != 0){
			return false;
		}
		return delegate.sets(keyvalues);
	}

	@Override
	public List<String> gets(String...cacheKey) throws CacheException{
		checkKey(cacheKey);
		return delegate.gets(cacheKey);
	}

	@Override
	public boolean mod(String cacheKey, String value)
			throws CacheException {
		checkKey(cacheKey);
		checkValue(value);
		return delegate.mod(cacheKey, value);
	}

	@Override
	public Long del(String cacheKey) throws CacheException {
		checkKey(cacheKey);
		return delegate.del(cacheKey);
	}

	@Override
	public String get(String cacheKey) throws CacheException {
		checkKey(cacheKey);
		return delegate.get(cacheKey);
	}

	@Override
	public Long addMembers(String groupKey, String... entityCacheKeys)
			throws CacheException {
		checkKey(groupKey);
		checkMembers(entityCacheKeys);
		return delegate.addMembers(groupKey, entityCacheKeys);
	}

	@Override
	public Long delMembers(String groupKey, String... entityCacheKeys)
			throws CacheException {
		checkKey(groupKey);
		checkMembers(entityCacheKeys);
		return delegate.delMembers(groupKey, entityCacheKeys);
	}

	@Override
	public Set<String> getMembers(String groupKey) throws CacheException {
		checkKey(groupKey);
		return delegate.getMembers(groupKey);
	}

	@Override
	public int getSize() {
		return delegate.getSize();
	}
	
	private void checkKey(String... keys)throws CacheException{
		if(null == keys || keys.length == 0){
			throw new CacheException("the key cannot be null");
		}
	}

	private void checkMembers(String...members)throws CacheException{
		if(members.length == 0){
			throw new CacheException("At least one member");
		}
	}
	
	private void checkValue(Object value)throws CacheException{
		if(null == value){
			throw new CacheException("Value cannot be null");
		}
	}
}
