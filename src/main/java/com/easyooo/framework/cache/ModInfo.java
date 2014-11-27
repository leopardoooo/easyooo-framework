package com.easyooo.framework.cache;

import java.util.Map;


/**
 * 
 * 为了减少缓存读取的次数，故封装一个修改的实体类可以返回修改前、修改后的值
 *
 * @author Killer
 */
public class ModInfo {
	
	/** 缓存Key */
	private String cacheKey;
	/** 旧值 */
	private Map<String, Object> oldMap;
	private Object oldBean;
	/** 新值  */
	private Map<String, Object> newMap;
	private Object newBean;
	
	public ModInfo(String cacheKey, Object oldBean, Map<String, Object> oldMap,
			Object newBean, Map<String, Object> newMap) {
		super();
		this.cacheKey = cacheKey;
		this.oldBean = oldBean;
		this.oldMap = oldMap;
		this.newMap = newMap;
		this.newBean = newBean;
	}
	
	public String getCacheKey() {
		return cacheKey;
	}

	public Map<String, Object> getOldMap() {
		return oldMap;
	}

	public Map<String, Object> getNewMap() {
		return newMap;
	}

	public Object getOldBean() {
		return oldBean;
	}

	public Object getNewBean() {
		return newBean;
	}
}
