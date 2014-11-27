/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.impl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.TypeReference;
import com.easyooo.framework.cache.CacheException;
import com.easyooo.framework.cache.CacheManager;
import com.easyooo.framework.cache.ConfigurationException;
import com.easyooo.framework.cache.KeyBuilder;
import com.easyooo.framework.cache.ModInfo;
import com.easyooo.framework.cache.config.CacheBean;
import com.easyooo.framework.cache.config.Configuration;
import com.easyooo.framework.cache.config.MergingBean;
import com.easyooo.framework.cache.config.impl.AnnotationConfiguration;
import com.easyooo.framework.cache.seriaziler.JsonSeriaziler;
import com.easyooo.framework.cache.seriaziler.SerializationException;
import com.easyooo.framework.cache.seriaziler.SeriazilerFactory;
import com.easyooo.framework.cache.storage.ICache;
import com.easyooo.framework.cache.util.CacheUtil;
import com.easyooo.framework.common.util.CglibUtil;
import com.easyooo.framework.common.util.MapUtil;

/**
 * 
 * 缓存的核心管理器实现
 * 
 * @author Killer
 */
public class CacheManagerImpl implements CacheManager{
	
	protected CacheChainBuilder cacheChain;
	protected Configuration configuration = new AnnotationConfiguration();
	private SeriazilerFactory factory = new SeriazilerFactory(new JsonSeriaziler());

	public CacheManagerImpl() {
	}
	
	public CacheManagerImpl(CacheChainBuilder cacheChain) {
		this.cacheChain = cacheChain;
	}

	@Override
	public String insert(Object bean) throws CacheException {
		final String cacheKey = buildCacheKey(bean);
		String value = seriazile(bean);
		if(buildCache(bean).set(cacheKey, value)){
			return cacheKey;
		}
		return null;
	}

	@Override
	public String updateByPrimaryKey(Object newBean) throws CacheException {
		final String cacheKey = buildCacheKey(newBean);
		String value = seriazile(newBean);
		if(buildCache(newBean).mod(cacheKey, value)){
			return cacheKey;
		}
		return null;
	}
	
	/**
	 * 修改部分值时，会从数据库中查找对应的所有的属性值，反序列化成Map，通
	 * 过比对将要修改的内容覆盖，最后通过重新设置的方式修改缓存
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ModInfo updateByPrimaryKeySelective(Object newBean) throws CacheException {
		final String cacheKey = buildCacheKey(newBean);
		ICache cache = buildCache(newBean);
		
		// get old object
		Object oldBean = deserialize(newBean, cache.get(cacheKey));
		if(oldBean == null){
			return null;
		}
		Map<String, Object> oldMap = CglibUtil.describe(oldBean);
		// new object
		Map<String, Object> newMap = CglibUtil.describe(newBean);
		// override old map
		Map<String, Object> unionMap = MapUtil.overrideExcludeNull(oldMap, newMap);
		
		// mod cache
		if(cache.mod(cacheKey, seriazile(unionMap))){
			return new ModInfo(cacheKey, oldBean, oldMap, newBean, newMap);
		}
		return null;
	}

	@Override
	public String deleteByPrimaryKey(Object bean) throws CacheException {
		final String cacheKey = buildCacheKey(bean);
		Long rows = buildCache(bean).del(cacheKey);
		if(rows == 0){
			return null;
		}
		return cacheKey;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T selectByPrimaryKey(Object bean) throws CacheException {
		// build the Key
		final String cacheKey = buildCacheKey(bean);
		// get a bean
		String value = buildCache(bean).get(cacheKey);
		if(value == null || "".equals(value)){
			return null;
		}
		return (T)deserialize(bean, value);
	}

	/**
	 * 该方法使用Gets读取所有的成员列表，效率取决于缓存的gets实现
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> selectByGroupKey(Object bean, String groupName)
			throws CacheException {
		String groupCacheKey = buildGroupCacheKey(bean, groupName);
		ICache cache = buildCache(bean);
		Set<String> memberCacheKeySet = cache.getMembers(groupCacheKey);
		if(memberCacheKeySet == null || memberCacheKeySet.size() == 0){
			return null;
		}
		List<T> dataList = new ArrayList<T>();
		List<String> memberValueList = cache.gets(memberCacheKeySet
				.toArray(new String[] {}));
		for (String memberValue : memberValueList) {
			dataList.add((T)deserialize(bean, memberValue));
		}
		return dataList;
	}
	
	@Override
	public boolean saveSingleGroup(Object bean, List<?> beanList,
			String groupName) throws CacheException {
		if(beanList == null || beanList.size() == 0){
			return true;
		}
		String groupCacheKey = buildGroupCacheKey(bean, groupName);
		
		String[] keyvalues = new String[beanList.size() * 2];
		String[] memberArray = new String[beanList.size()];
		for (int i= 0, j = 0; i< beanList.size(); i++, j+=2) {
			Object itemBean = beanList.get(i);
			String cacheKey = buildCacheKey(itemBean);
			memberArray[i] = cacheKey;
			keyvalues[j] = cacheKey;
			keyvalues[j + 1] = seriazile(itemBean);
		}
		// set cache
		ICache cache = buildCache(bean);
		if(cache.sets(keyvalues)){
			cache.addMembers(groupCacheKey, memberArray);
			return true;
		}
		return false;
	}

	/**
	 * 将多个Bean合并到一个Bean中，如果存在同名的属性则被覆盖,如果合并时，
	 * 主要的Bean必须存在缓存,否则返回NULL
	 */
	@Override
	public List<Map<String,Object>> selectMergingObjectByPrimaryKey(Object bean, Class<?> dtoClass)
			throws CacheException {
		if(dtoClass == null)
			return null;
		CacheBean cacheBean = getCacheBean(bean);
		Map<Class<?>, MergingBean> mbean = cacheBean.getMergings();
		if(mbean == null)
			CacheException.throwe("The entity bean merge no configuration");
		MergingBean mb = mbean.get(dtoClass);
		if(mb == null)
			CacheException.throwe("No configuration mapping bean");
		// get from cache
		Map<String,Object> masterBean = getCacheAndDeserializeToMap(bean);
		List<Object> configBeans = CacheUtil.afterSetKeyRefObjects(masterBean, mb.getRefrences());
		
		// add object
		List<Map<String,Object>> target = new ArrayList<>();
		target.add(masterBean);
		for (Object refBean : configBeans) {
			target.add(getCacheAndDeserializeToMap(refBean));
		}
		return target;
	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// key build functions
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	protected List<String> buildCacheKeys(List<Object> configBeans)throws CacheException {
		List<String> refCacheKey = new ArrayList<String>();
		for (Object bean : configBeans) {
			refCacheKey.add(buildCacheKey(bean));
		}
		return refCacheKey;
	}
	
	protected String buildCacheKey(Object bean)throws CacheException {
		return buildCacheKey(bean, bean.getClass());
	}
	
	protected String buildCacheKey(Object bean, Class<?> cacheClass)throws CacheException {
		CacheBean cacheBean = getCacheBean(cacheClass);
		KeyBuilder builder = newKeyBuilder(cacheBean);
		try {
			String[] keyValues = CacheUtil.getKeyValues(bean, cacheBean);
			if(!CacheUtil.checkKeyValues(cacheBean, keyValues)){
				throw new CacheException("cannot generate the primary key, via the entity bean attribute value");
			}
			return builder.buildKey(cacheBean, keyValues);
		} catch (Exception e) {
			throw new CacheException("Unable to generate entity cache key", e);
		}
	}
	
	protected String buildGroupCacheKey(Object bean, String group)throws CacheException{
		CacheBean cacheBean = getCacheBean(bean);
		KeyBuilder builder = newKeyBuilder(cacheBean);
		
		if(Configuration.DEFAULT_MINI_TABLE.equals(group)){
			return buildMiniTableGroupKey(cacheBean, builder, group);
		}
		return buildGroupCacheKey(bean, cacheBean, builder, group);
	}
	
	protected String buildMiniTableGroupKey(CacheBean cacheBean,
			KeyBuilder builder, String group) throws CacheException {
		return builder.buildMiniTableKey(cacheBean);
	}
	
	protected String buildGroupCacheKey(Object bean,CacheBean cacheBean,
			KeyBuilder builder, String group)throws CacheException {
		try {
			String[] groupValues = CacheUtil.getGroupValues(bean, cacheBean, group);
			if(!CacheUtil.checkGroupValues(cacheBean, group, groupValues)){
				throw new CacheException("cannot generate the group key, via the entity bean attribute value");
			}
			return builder.buildGroupKey(cacheBean, group, groupValues);
		} catch (Exception e) {
			throw new CacheException("Unable to generate group cache key", e);
		}
	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// important methods
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	@Override
	public CacheBean getCacheBean(Object bean) throws CacheException {
		Class<?> cacheClass = null;
		if( bean instanceof Class){
			cacheClass = (Class<?>) bean;
		}else{
			cacheClass = bean.getClass();
		}
		try {
			return configuration.get(cacheClass);
		} catch (ConfigurationException e) {
			throw new CacheException(e);
		}
	}
	
	private Map<String, Object> getCacheAndDeserializeToMap(Object bean)throws CacheException{
		final String cacheKey = buildCacheKey(bean);
		// get a map from cache
		String value = buildCache(bean).get(cacheKey);
		return deserializeToMap(value);
	}
	
	protected Map<String,Object> deserializeToMap(String value)throws CacheException{
		try {
			Type type = new TypeReference<Map<String,Object>>(){}.getType();
			return factory.deserializeAsObject(value, type);
		} catch (SerializationException e) {
			throw new CacheException(e);
		}
	}
	
	/**
	 * 从缓存查找将序列化值，再反序列化成对象
	 */
	protected Object deserialize(Object bean, String value)throws CacheException{
		try {
			return factory.deserializeAsObject(value, bean.getClass());
		} catch (SerializationException e) {
			throw new CacheException(e);
		}
	}
	
	/**
	 * 将对象序列化
	 */
	protected String seriazile(Object bean) throws CacheException {
		try {
			return factory.seriazileAsString(bean);
		} catch (SerializationException e) {
			throw new CacheException(e);
		}
	}

	protected KeyBuilder newKeyBuilder(CacheBean cacheBean)
			throws CacheException {
		try {
			return cacheBean.getKeyBuilder().newInstance();
		} catch (Exception e) {
			throw new CacheException("Unable to create the instance of primary key generators", e);
		}
	}
	
	protected ICache buildCache(Object bean)throws CacheException{
		return cacheChain.buildCache(getCacheBean(bean));
	}
	
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void setSeriazilerFactory(SeriazilerFactory seriazilerFactory) {
		this.factory = seriazilerFactory;
	}
}
