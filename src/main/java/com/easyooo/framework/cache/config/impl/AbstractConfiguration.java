/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.config.impl;

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easyooo.framework.cache.CacheException;
import com.easyooo.framework.cache.ConfigurationException;
import com.easyooo.framework.cache.IKeyValue;
import com.easyooo.framework.cache.annotations.ThreadSafety;
import com.easyooo.framework.cache.config.CacheBean;
import com.easyooo.framework.cache.config.Configuration;
import com.easyooo.framework.cache.config.MergingBean;
import com.easyooo.framework.common.util.ClassUtil;

/**
 * <p>缓存配置抽象类实现，使用线程安全的Map，实现该类无需关心存储以及存储是否安全，
 * 还具备缓存配置的合法性检查，扩展该类只需要完成具体的解析配置的过程</p>
 * 
 * @see #parserConfiguration(Class)
 * @see ConcurrentHashMap
 * 
 * @author Killer
 */
@ThreadSafety
public abstract class AbstractConfiguration implements Configuration{
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final ConcurrentHashMap<Class<?>, Future<CacheBean>> cache 
		= new ConcurrentHashMap<Class<?>, Future<CacheBean>>();

	public AbstractConfiguration(){
		super();
	}
	
	/**
	 * 从缓存配置中获取一个配置信息，如果不存在，则动态解析它，如何解析则取决于具体实现类
	 * 
	 * @param type 实体类的Class
	 * @return 能正常返回就一定能返回一个正确的CacheBean
	 * @throws ConfigurationException
	 */
	@Override
	public CacheBean get(final Class<?> type)
			throws ConfigurationException {
		if(type == null) 
			return null;
		// Get from cacheMap
		Future<CacheBean> future = cache.get(type);
		if(future == null){
			Callable<CacheBean> eval = new Callable<CacheBean>() {
				@Override
				public CacheBean call() throws Exception {
					return parserConfiguration(type);
				}
			};
			
			FutureTask<CacheBean> ft = new FutureTask<CacheBean>(eval);
			future = cache.putIfAbsent(type, ft);
			if(future == null){
				future = ft;
				ft.run();
			}
		}
		
		try {
			return future.get();
		} catch (InterruptedException e) {
			cache.remove(type);
			throw new ConfigurationException(e);
		} catch (ExecutionException e) {
			cache.remove(type);
			throw new ConfigurationException("Load [" + type.getName() + "] configuration error", e);
		}
	}
	
	/**
	 * @see #get(Class)
	 * 区别在于，如果不存在，则直接返回NULL
	 * @throws CacheException 不包含自动配置的特性，故抛出 CacheException，
	 * 	它仅仅是InterruptedException异常的薄包装
	 */
	public CacheBean getIfExist(final Class<?> type)throws CacheException{
		Future<CacheBean> future = cache.get(type);
		if(null == future){
			return null;
		}
		if(future.isDone()){
			try {
				return future.get();
			} catch (InterruptedException e) {
				throw new CacheException(e);
			} catch (ExecutionException igore) {
			}
		}
		return null;
	}
	
	/**
	 * 卸载一个Cache Class，该方法一般不会主动调用，该方法只能与
	 * @param type
	 * @return
	 */
	public boolean unloadCacheClass(final Class<?> type){
		if(null == cache.remove(type)){
			return false;
		}
		return true;
	}
	
	/**
	 * 验证缓存配置是否正确
	 * 
	 * @param target
	 * @throws ConfigurationException
	 */
	protected CacheBean validate(CacheBean target) throws ConfigurationException {
 
		if (target == null || target.getXbeanClass() == null) {
			throw new ConfigurationException("Cache annotation was not found");
		}

		if (target.getMergings() != null) {
			Set<Entry<Class<?>, MergingBean>> entries = target.getMergings().entrySet();
			for (Entry<Class<?>, MergingBean> entry : entries) {
				if(entry.getValue().getRefrences().size() == 0){
					throw new ConfigurationException(
							"Merging annotation was found, but refrences size is zero");
				}
			}
		}

		if (target.getKeyPropertyList().size() == 0
				&& !ClassUtil.hasInterface(target.getXbeanClass(), IKeyValue.class)) {
			throw new ConfigurationException(
					"KeyProperty and Key interface was not found");
		}
		
		if (target.getFields().size() == 0) {
			throw new ConfigurationException(
					"The field does not need to cache");
		}

		return target;
	}
}
