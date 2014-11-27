/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache;

import java.util.List;
import java.util.Map;

import com.easyooo.framework.cache.config.CacheBean;



/**
 * 缓存数据的核心管理器接口定义
 * 
 * <p>
 * 该接口中的所有方法都依赖<code>CacheBean</code>，
 * 调用者必须保证所传入的<code>bean</code>是一个配置了正确的<code>@Cache</code>
 * 同时根据不同的接口方法，还需要注入不同的参数，详细说明参考接口方法的注释
 * </p>
 * 
 * @see com.yaochen.boss.data.cache.annotations.Cache
 * @see com.yaochen.boss.data.cache.config.CacheBean
 * 
 * @author Killer
 */
public interface CacheManager {

	/**
	 * 将数据保存到缓存
	 * 
	 * @param bean 需要设置KEY值(多个)
	 * @return 返回主键值
	 * @throws CacheException
	 */
	public String insert(Object bean) throws CacheException;

	/**
	 * 修改缓存中全部的数据，包括null值
	 * @param bean 需要设置KEY值(多个)及需要修改的新值
	 * @throws CacheException
	 */
	public String updateByPrimaryKey(Object bean) throws CacheException;

	
	/**
	 * 选择性的去修改，如果属性值为null则忽略不修改
	 * 
	 * @param bean
	 * @return 实现类必须设置 新值、旧值、缓存主键
	 * @throws CacheException
	 */
	public ModInfo updateByPrimaryKeySelective(Object bean) throws CacheException;
	
	/**
	 * 将对象从缓存中移除
	 * 
	 * @param bean 需要设置KEY值(多个)
	 * @return 返回真实的主键值
	 * @throws CacheException
	 */
	public String deleteByPrimaryKey(Object bean) throws CacheException;
	
	/**
	 * 从缓存中查找数据
	 * 
	 * @param bean 需要设置KEY值(多个)
	 * @return
	 * @throws CacheException
	 */
	public <T> T selectByPrimaryKey(Object bean) throws CacheException;
	
	
	/**
	 * 通过分组值获取一组缓存数据，需要分组名称
	 * 
	 * @param bean 需要设置Group KEY值(多个)
	 * @param groupName 别名
	 * @return
	 * @throws CacheException
	 */
	public <T> List<T> selectByGroupKey(Object bean, String groupName) throws CacheException;
	
	
	/**
	 * 同步单个分组至服务器
	 * @param beanList 某个指定分组的数据
	 * @param groupName 分组名称
	 */
	public boolean saveSingleGroup(Object bean, List<?> beanList, String groupName)throws CacheException;
	
	/**
	 * 获取一个组合实体类，根据配置中的<code>MergingBean</code>
	 * 
	 * @param bean 需要设置KEY值(多个)
	 * @return
	 * @throws CacheException
	 */
	public List<Map<String,Object>> selectMergingObjectByPrimaryKey(Object bean, Class<?> dtoClass)
			throws CacheException;
	
	/**
	 * 
	 * @param bean
	 * @return
	 * @throws CacheException
	 */
	public CacheBean getCacheBean(Object bean) throws CacheException;
	
}
