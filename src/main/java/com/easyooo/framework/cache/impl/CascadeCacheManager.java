/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.impl;

import static com.easyooo.framework.cache.impl.CascadeOperation.getDeleteOps;
import static com.easyooo.framework.cache.impl.CascadeOperation.getInsertOps;
import static com.easyooo.framework.cache.impl.CascadeOperation.getUpdateOps;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easyooo.framework.cache.CacheException;
import com.easyooo.framework.cache.ModInfo;
import com.easyooo.framework.cache.config.CacheBean;
import com.easyooo.framework.cache.config.GroupBean;
import com.easyooo.framework.cache.storage.ICache;
import com.easyooo.framework.common.util.CglibUtil;

/**
 * 缓存的级联管理, 主要维护组的级联关系
 * @author Killer
 */
public class CascadeCacheManager extends CacheManagerImpl{
	Logger logger = LoggerFactory.getLogger(getClass());
	
	public CascadeCacheManager(){
	}
	
	public CascadeCacheManager(CacheChainBuilder cacheChain) {
		super(cacheChain);
	}

	@Override
	public String insert(Object bean) throws CacheException {
		final String cacheKey = super.insert(bean);
		if(cacheKey == null)
			return cacheKey;
		// check has more groups
		Map<String, List<GroupBean>> groups = getGroups(bean);
		if(groups == null || groups.size() == 0)
			return cacheKey;
		doCascadeOps(cacheKey, bean, null, getInsertOps(groups));
		return cacheKey;
	}

	/**
	 * @see super{@link #updateByPrimaryKey(Object)}
	 * 如果修改的缓存数据存在分组信息并且分组属性值也需要修改，
	 * 这个时候就必须保证分组缓存数据的正确性
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String updateByPrimaryKey(Object newBean) throws CacheException {
		// check has more groups
		Map<String, List<GroupBean>> groups = getGroups(newBean);
		if(groups == null || groups.size() == 0){
			return super.updateByPrimaryKey(newBean);
		}
		// exist groups
		Object oldBean = selectByPrimaryKey(newBean);
		String cacheKey = super.updateByPrimaryKey(newBean);
		if(cacheKey == null || oldBean == null){
			return null;
		}
		Map<String, Object> oldMap = CglibUtil.describe(oldBean);
		Map<String, Object> newMap = CglibUtil.describe(newBean);
		ModInfo mod = new ModInfo(cacheKey, oldBean, oldMap, newBean, newMap);
		return updateGroupObject(mod, groups, false).getCacheKey();
	}
	
	/**
	 * @see #updateByPrimaryKey(Object) 排除分组属性值为null的分组
	 */
	@Override
	public ModInfo updateByPrimaryKeySelective(Object newBean) throws CacheException {
		ModInfo mod = super.updateByPrimaryKeySelective(newBean);
		if(mod == null){
			return null;
		}
		// check groups & get default group cacheKey by parameter bean
		Map<String, List<GroupBean>> groups = getGroups(newBean);
		if(groups == null || groups.size() == 0){
			return mod;
		}
		return updateGroupObject(mod, groups, true);
	}
	
	/**
	 * 级联修改对象，selectiveMode 是否选择性的去修改 
	 */
	private ModInfo updateGroupObject(ModInfo mod, Map<String, List<GroupBean>> groups, boolean selectiveMode)throws CacheException{
		Map<String, Object> oldMap = mod.getOldMap();
		Map<String, Object> newMap = mod.getNewMap();
		final String cacheKey = mod.getCacheKey();
		Object newBean = mod.getNewBean();
		Object oldBean = mod.getOldBean();
		List<CascadeOperation> operationList = getUpdateOps(groups,
				oldMap, newMap, selectiveMode);
		// cascade
		doCascadeOps(cacheKey, newBean, oldBean, operationList);
		return mod;
	}
	
	/**
	 * <p>删除缓存数据并清除分组信息</p>
	 * <p>如果存在分组，此时就会从缓存中查找到这些值，触发额外的1次Socket</p>
	 */
	@Override
	public String deleteByPrimaryKey(Object bean) throws CacheException {
		// check groups & get default group cacheKey by parameter bean
		Map<String, List<GroupBean>> groups = getGroups(bean);
		if(groups == null)
			return super.deleteByPrimaryKey(bean);
		// exist groups
		Object oldBean = this.selectByPrimaryKey(bean);
		// remove
		final String cacheKey = super.deleteByPrimaryKey(bean);
		if(cacheKey == null || oldBean == null)
			return cacheKey;
		// cascade
		doCascadeOps(cacheKey, bean, oldBean, getDeleteOps(groups));
		return cacheKey;
	}
	
	/**
	 * 级联操作实现
	 */
	protected void doCascadeOps(String cacheKey, Object newBean, Object oldBean,
			List<CascadeOperation> cascades) throws CacheException {
		if(cascades == null || cascades.size() == 0){
			return ;
		}
		ICache cache = buildCache(newBean);
		for (CascadeOperation op : cascades) {
			try{
				String group = op.getGroup();
				switch (op.getOperation()) {
				case DELETE:
					cache.delMembers(buildGroupCacheKey(oldBean, group), cacheKey);
					break;
				case DELETE_INSERT:
					cache.delMembers(buildGroupCacheKey(oldBean, group), cacheKey);
					// 执行完不跳出，继续执行INSERT CASE
				case INSERT:
					String groupKey = buildGroupCacheKey(newBean, group);
					if(existGroup(cache, groupKey)){
						cache.addMembers(groupKey, cacheKey);
					}
					break;
				default:
					break;
				}
			}catch(CacheException e){
				logger.error("Cascade operation failed", e);
			}
		}
	}
	
	private boolean existGroup(ICache cache, String groupKey)throws CacheException{
		Set<String> mems = cache.getMembers(groupKey);
		return mems == null || mems.size() == 0 ? false : true; 
	}
	
	private Map<String, List<GroupBean>> getGroups(Object bean)throws CacheException{
		CacheBean cacheBean = getCacheBean(bean);
		Map<String, List<GroupBean>> groups = cacheBean.getGroups();
		if (groups == null || groups.size() == 0) {
			return null;
		}
		return groups;
	}
}
