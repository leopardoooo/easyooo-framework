/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache;

import com.easyooo.framework.cache.config.CacheBean;

/**
 * 缓存主键生成器接口定义，可自行扩展该接口实现自己的Key生成规则
 * 
 * @author Killer
 */
public interface KeyBuilder {

	/**
	 * 生成缓存主键通过缓存配置， 
	 * 必须保证 {@link CacheBean#getKeyPropertyList()}已配置。
	 * 
	 * @param cacheBean 
	 * @param primaryKeys 主键值，支持复合主键
	 * @return
	 */
	String buildKey(CacheBean cacheBean, String ...primaryKeys);
	
	
	/**
	 * 生成一组缓存主键通过主键值以及缓存的配置，
	 * 必须保证 <code>{@link CacheBean#getMergingBean()}</code>是合法的，
	 * 合并的Class中，必须保证实体类的缓存主键配置是一致的，
	 * 否则将生成错误的Cache KEY, 导致合并失败
	 * 
	 * @param cacheBean
	 * @param primaryKeys 缓存实体类的主键值，支持复合主键
	 * @return
	 */
	//String[] buildMergingKeys(CacheBean cacheBean, String...primaryKeys);
	
	
	/**
	 * 生成一个分组列表格式的缓存主键，给定的缓存配置，
	 * 必须保证<code>{@link CacheBean#getGroupList()}</code>正确
	 * 
	 * @param cacheBean
	 * @return
	 */
	String buildGroupKey(CacheBean cacheBean, String groupName, String...groupKeys);
	
	
	/**
	 * 生成gets小表所有数据格式的缓存主键
	 * @param cacheBean
	 * @return
	 */
	String buildMiniTableKey(CacheBean cacheBean);
	
}
