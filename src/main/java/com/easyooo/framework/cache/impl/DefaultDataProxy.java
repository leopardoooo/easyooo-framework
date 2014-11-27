/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easyooo.framework.cache.CacheException;
import com.easyooo.framework.cache.CacheManager;
import com.easyooo.framework.cache.DataProxyException;
import com.easyooo.framework.cache.Delegater;
import com.easyooo.framework.cache.PropertiesMerging;

/**
 * 
 * 数据代理实现了持久化操作类与缓存的管理器的集成， 该类始终依赖CacheManager完成对缓存的维护， 并依赖
 * <code>Delegater</code>完成数据的持久化
 * 
 * @see CacheManager
 * @see Delegater
 * 
 * @author Killer
 */
public class DefaultDataProxy {
	
	Logger logger = LoggerFactory.getLogger(getClass());

	private CacheManager cacheManager;

	/**
	 * 为Target构造代理实例，使它具备缓存的管理
	 * @param cacheManager 缓存管理器
	 */
	public DefaultDataProxy(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public Integer insert(final Object bean, Delegater<Integer> target)
			throws DataProxyException {
		Integer rows = target.execute();
		doUpdateCallback(rows, bean, new UpdateCallback() {
			public void doCallback() throws CacheException {
				cacheManager.insert(bean);
			}
		});
		return rows;
	}

	public Integer updateByPrimaryKey(final Object bean, Delegater<Integer> target)
			throws DataProxyException {
		Integer rows = target.execute();
		doUpdateCallback(rows, bean, new UpdateCallback() {
			public void doCallback() throws CacheException {
				cacheManager.updateByPrimaryKey(bean);
			}
		});
		return rows;
	}
	
	public Integer updateByPrimaryKeySelective(final Object bean, Delegater<Integer> target)
			throws DataProxyException {
		Integer rows = target.execute();
		doUpdateCallback(rows, bean, new UpdateCallback() {
			public void doCallback() throws CacheException {
				cacheManager.updateByPrimaryKeySelective(bean);
			}
		});
		return rows;
	}

	public Integer deleteByPrimaryKey(final Object bean, Delegater<Integer> target)
			throws DataProxyException {
		Integer rows = target.execute();
		doUpdateCallback(rows, bean, new UpdateCallback() {
			public void doCallback() throws CacheException {
				cacheManager.deleteByPrimaryKey(bean);
			}
		});
		return rows;
	}

	public <T> T selectByPrimaryKey(final Object bean, Delegater<T> target)
			throws DataProxyException {
		return doSelectCallback(bean, target, new SelectCallbackAdapter<T>() {
			@Override
			public T doSelectFromCache() throws CacheException {
				return cacheManager.selectByPrimaryKey(bean);
			}
			@Override
			public void doSynch(T t) throws CacheException {
				cacheManager.insert(t);
			}
		});
	}

	public <T> List<T> selectByGroupKey(final Object bean, final String group, Delegater<List<T>> target)
			throws DataProxyException {
		return doSelectCallback(bean, target, new SelectCallback<List<T>>() {
			@Override
			public List<T> doSelectFromCache() throws CacheException {
				return cacheManager.selectByGroupKey(bean, group);
			}
			@Override
			public void doSynch(List<T> t) throws CacheException {
				cacheManager.saveSingleGroup(bean, t, group);
			}
			/**
			 * 如果数据集合是null或者部分对象为null，则视为缓存没有命中，会自动从调用
			 * @see #doSynch
			 * @param dataList
			 * @return
			 * @throws CacheException
			 */
			@Override
			public boolean hasData(List<T> dataList){
				if(dataList != null){
					for (T t2 : dataList) {
						if(t2 == null){
							return false;
						}
					}
					return true;
				}
				return false;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <T> T selectMergingByPrimaryKey(final Object bean,
			Delegater<T> target, final Class<T> dtoClass) throws DataProxyException {
		return doSelectCallback(bean, target, new SelectCallbackAdapter<T>() {
			@Override
			public T doSelectFromCache() throws CacheException {
				List<Map<String, Object>> values = cacheManager
						.selectMergingObjectByPrimaryKey(bean, dtoClass);
				Object dto = newMergingObject(dtoClass);
				PropertiesMerging pm = getMergingImplementObject(dtoClass);
				Object o = pm.merge(dto, values);
				return o != null ? (T)o : null;
			}
			
			@Override
			public void doSynch(T t) throws CacheException {
				// TODO Does not support
			}
			
			private PropertiesMerging getMergingImplementObject(Object dto){
				if(dto instanceof PropertiesMerging){
					return (PropertiesMerging)dto;
				}else{
					return new OverridePropertiesMerging();
				}
			}
		});
	}
	
	private interface UpdateCallback{
		public void doCallback()throws CacheException;
	}
	private interface SelectCallback<T>{
		public T doSelectFromCache()throws CacheException;
		public void doSynch(T t)throws CacheException;
		public boolean hasData(T t);
	}
	
	private abstract class SelectCallbackAdapter<T> implements SelectCallback<T>{
		@Override
		public boolean hasData(T t){
			return t != null;
		}
	}
	
	private void doUpdateCallback(Integer rows, Object bean, UpdateCallback callback)throws DataProxyException{
		try {
			if (rows == 1) {
				if(logger.isDebugEnabled()){
					logger.debug("Cache["+ bean +"] synchronous..");
				}
				callback.doCallback();
			}
		} catch (CacheException e) {
			handleCacheException(e);
		}
	}
	
	private <T> T doSelectCallback(Object bean, Delegater<T> target,SelectCallback<T> callback)throws DataProxyException{
		T t = null;
		try {
			t = callback.doSelectFromCache();
		} catch (CacheException e) {
			handleCacheException(e);
		}
		if (!callback.hasData(t)) {
			if(logger.isDebugEnabled()){
				logger.debug("Delegated to the database["+ bean +"]");
			}
			t = target.execute();
			if (t != null) {
				try {
					callback.doSynch(t);
				} catch (CacheException e) {
					handleCacheException(e);
				}
			}
		}
		return t;
	}
	
	private Object newMergingObject(Class<?> dtoClass)throws CacheException{
		try {
			return dtoClass.newInstance();
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}

	private void handleCacheException(CacheException ce)
			throws DataProxyException {
		logger.error("An error occurred in the operation of the cache：", ce);
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
}
