/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.easyooo.framework.cache.CacheException;
import com.easyooo.framework.common.util.ListUtil;

/**
 * 
 * JVM local cache implementions
 * 
 * @author Killer
 */
public class JVMCache implements ICache{

	private final static Map<String, Object> cache 
			 	= new ConcurrentHashMap<String, Object>();
	
	// private ReadWriteLock readWriterLock = new ReentrantReadWriteLock();
	
	private ICache delegate;

	public JVMCache(){
		super();
	}
	
	public JVMCache(ICache delegate){
		super();
		setDelegate(delegate);
	}
	
	public boolean set(String cacheKey, String value)
			throws CacheException{
		cache.put(cacheKey, value);
		if(delegate != null){
			return delegate.set(cacheKey, value);
		}
		return true;
	}

	@Override
	public List<String> gets(String...cacheKeys) throws CacheException{
		List<String> strings = new ArrayList<String>();
		for (String cacheKey : cacheKeys) {
			Object o = cache.get(cacheKey);
			if(o != null)
				strings.add(o.toString());
			else{
				String value = null;
				if(delegate != null){
					value = ListUtil.getFirstElement(delegate.gets(cacheKey));
					if(value != null){
						cache.put(cacheKey, value);
					}
				}
				strings.add(value);
			}
		}
		return strings; 
	}
	
	@Override
	public boolean sets(String... keyvalues) throws CacheException{
		for (int i = 0; i < keyvalues.length; i+=2) {
			String cacheKey = keyvalues[i];
			String value = keyvalues[i+1];
			cache.put(cacheKey, value);
		}
		if(delegate != null){
			return delegate.sets(keyvalues);
		}
		return true;
	}

	@Override
	public boolean mod(String cacheKey, String value)
			throws CacheException {
		cache.put(cacheKey, value);
		if(delegate != null){
			return delegate.mod(cacheKey, value);
		}
		return true;
	}

	@Override
	public Long del(String cacheKey) throws CacheException {
		cache.remove(cacheKey);
		if(delegate != null){
			return delegate.del(cacheKey);
		}
		return 1L;
	}

	@Override
	public String get(String cacheKey) throws CacheException {
		Object o = cache.get(cacheKey);
		if(o != null){
			return o.toString();
		}
		
		if(delegate != null){
			String value = delegate.get(cacheKey);
			if(value != null){
				cache.put(cacheKey, value);
			}
			return value;
		}
		return null;
	}

	@Override
	public Long addMembers(String groupKey, String... entityCacheKeys)
			throws CacheException {
		Object o = cache.get(groupKey);
		Set<String> mems = null;
		if(o == null){
			if(delegate != null){
				mems = delegate.getMembers(groupKey);
			}
			if(mems == null){
				mems = newMemberContainer();
			}
			cache.put(groupKey, mems);
		}else{
			mems = castToSet(groupKey, o);
		}
		List<String> successList = new ArrayList<String>();
		for (String eck : entityCacheKeys) {
			if(!mems.contains(eck)){
				mems.add(eck);
				successList.add(eck);
			}
		}
		if(delegate != null){
			delegate.addMembers(groupKey, entityCacheKeys);
		}
		return new Long(successList.size());
	}

	@Override
	public Long delMembers(String groupKey, String... entityCacheKeys)
			throws CacheException {
		Object o = cache.get(groupKey);
		Set<String> mems = null;
		if(o == null){
			logger.warn("The group jvm cache["+ groupKey +"] does not exist.");
		}else{
			mems = castToSet(groupKey, o);
			List<String> successList = new ArrayList<String>();
			for (String eck : entityCacheKeys) {
				if(mems.remove(eck)){
					successList.add(eck);
				}
			}
		}
		if(delegate != null){
			delegate.delMembers(groupKey, entityCacheKeys);
		}
		return new Long(entityCacheKeys.length);
	}

	@Override
	public Set<String> getMembers(String groupKey) throws CacheException {
		Set<String> innerSet = null;
		Object o = cache.get(groupKey);
		if(o == null){
			if(delegate != null){
				innerSet = delegate.getMembers(groupKey);
				cache.put(groupKey, innerSet);
			}
		}else{
			innerSet = castToSet(groupKey, o);
		}
		return innerSet == null ? null : new CopyOnWriteArraySet<>(innerSet);
	}
	
	@SuppressWarnings("unchecked")
	public static HashSet<String> castToSet(String groupKey, Object o)throws CacheException{
		if(o instanceof HashSet){
			return (HashSet<String>)o;
		}
		throw new CacheException("The cache["+ groupKey +"] is not a collection");
	}
	
	public static Set<String> newMemberContainer(){
		return new HashSet<String>();
	}
	
	public Map<String, Object> getCacheObject(){
		return cache;
	}
	
	@Override
	public int getSize(){
		return cache.size();
	}
	
	protected void setDelegate(ICache delegate) {
		if(delegate != null && delegate != this){
			this.delegate = delegate;
		}
	}
}
