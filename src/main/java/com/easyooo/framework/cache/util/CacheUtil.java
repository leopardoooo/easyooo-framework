/*
 * Copyright © 2014 easyooo Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.easyooo.framework.cache.CacheException;
import com.easyooo.framework.cache.IGroup;
import com.easyooo.framework.cache.IKeyValue;
import com.easyooo.framework.cache.config.CacheBean;
import com.easyooo.framework.cache.config.GroupBean;
import com.easyooo.framework.cache.config.KeyPropertyBean;
import com.easyooo.framework.cache.config.RefrenceBean;
import com.easyooo.framework.common.util.CglibUtil;

/**
 * Cache Utils
 * 
 * @author Killer
 */
public class CacheUtil {
	

	enum OperationType{NORMAL, GROUP}
	
	/**
	 * <p>通过CacheBean & 实体类对象 ，
	 * 获取实体类所配置<code>@KeyProperty</code>属性实际的值并存入数组返回</p>
	 * <p>
	 * 	这里采用了一些小小的优化措施，如果实体类实现了IKeyValue接口，
	 *  则函数优先通过接口获取主键值，否则将采用反射机制获取主键值。
	 * </p>
	 * @param o 带有Cache的实体类
	 * @param cacheBean 配置好的
	 * @param groupName 组名
	 * @return
	 */
	public static String[] getKeyValues(Object o,
			CacheBean cacheBean) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if(o == null || cacheBean == null){
			return null;
		}
		
		// via the interface to get the primary key values
		if (o instanceof IKeyValue) {
			return ((IKeyValue) o).getKeyValues();
		}

		String []names = convertKeyProperty(cacheBean);
		return getKeywordValues(o, names);
	}
	
	
	/**
	 * <p>通过CacheBean & 实体类对象 以及所配置的<b>groupName</b>，
	 * 获取实体类所配置<code>@Group</code>属性实际的值并存入数组返回</p>
	 * <p>
	 * 	同样采用了优化措施，如果实体类实现了IGroup接口，
	 *  则函数优先通过接口获取组值，否则将采用反射机制获取组值。
	 * </p>
	 * @param o 带有Cache的实体类
	 * @param cacheBean 配置好的
	 * @param groupName 组名
	 * @return
	 */
	public static String[] getGroupValues(Object o, CacheBean cacheBean,
			String groupName) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if(o == null || cacheBean == null || groupName == null){
			return null;
		}
		
		// via the interface to get the group values
		if (o instanceof IGroup) {
			return ((IGroup) o).getGroupValues(groupName);
		}

		// Reflection to obtain
		String []names = convertGroupProperty(cacheBean, groupName);
		return getKeywordValues(o, names);
	}
	
	public static String[] getKeywordValues(Object o, String[] names){
		String[] strValues = new String[names.length];
		Object[] values = CglibUtil.getPropertyValue(o, names);
		int i = 0;
		for (Object obj: values) {
			strValues[i++] = (obj == null) ? null : obj.toString();
		}
		
		return strValues;
	}
	
	/**
	 * 通过CacheBean 获取到实体类所配置的<code>@KeyProperty</code>
	 * 注解的属性名称，存入字符数组并返回
	 * @param bean
	 * @return
	 */
	public static String[] convertKeyProperty(CacheBean bean){
		if(bean == null){
			return null;
		}
		List<KeyPropertyBean> keyBeans = bean.getKeyPropertyList();
		if(keyBeans == null){
			return new String[]{};
		}
		String[] keyArrs = new String[keyBeans.size()];
		int i = 0;
		for (KeyPropertyBean hpb : keyBeans) {
			keyArrs[i++] = hpb.getPropertyName();
		}
		return keyArrs;
	}
	
	/**
	 * 通过CacheBean 获取到实体类所配置的<code>@Group</code>
	 * 注解的属性名称，存入字符数组并返回
	 * @param bean
	 * @return
	 */
	public static String[] convertGroupProperty(CacheBean bean, String groupName){
		if(bean == null || groupName == null){
			return null;
		}
		if(bean.getGroups() == null){
			return new String[]{};
		}
		List<GroupBean> groupBeans = bean.getGroups().get(groupName);
		if(groupBeans == null){
			return new String[]{}; 
		}
		String[] groupArrs = new String[groupBeans.size()];
		
		int i = 0;
		for (GroupBean hpb : groupBeans) {
			groupArrs[i++] = hpb.getPropertyName();
		}
		return groupArrs;
	}
	
	
	public static boolean checkKeyValues(CacheBean bean, String[]keyValues){
		if(keyValues == null){
			return false;
		}
		List<KeyPropertyBean> kps = bean.getKeyPropertyList();
		if(kps.size() != keyValues.length){
			return false;
		}
		
		for (String vstr : keyValues) {
			if(null == vstr || "".equals(vstr)){
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean checkGroupValues(CacheBean bean, String groupName, String[]groupValues){
		if(groupValues == null || groupValues.length == 0){
			return false;
		}
		List<GroupBean> gbs = bean.getGroups().get(groupName);
		if(gbs.size() != groupValues.length){
			return false;
		}
		for (String vstr : groupValues) {
			if(null == vstr || "".equals(vstr)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 在map提取主键值根据配置，并返回所有的设置主键值后的配置Bean
	 * @param refrenceMap
	 * @return
	 */
	public static List<Object> afterSetKeyRefObjects(
			Map<String, Object> masterValues,
			Map<Class<?>, List<RefrenceBean>> refrenceMap)
			throws CacheException {
		List<Object> configBeans = new ArrayList<Object>();
		for (Map.Entry<Class<?>, List<RefrenceBean>> entry : refrenceMap
				.entrySet()) {
			configBeans.add(setPrimaryKeyRefBean(masterValues, entry));
		}
		return configBeans;
	}
	
	/**
	 * 设置主键值根据关联的Bean配置
	 */
	private static Object setPrimaryKeyRefBean(Map<String, Object> props,
			Map.Entry<Class<?>, List<RefrenceBean>> entry)
			throws CacheException {
		if(entry == null){
			return null;
		}
		Class<?> target = entry.getKey();
		List<RefrenceBean> refBeanList = entry.getValue();
		try {
			Object bean = target.newInstance();
			String[] names = new String[refBeanList.size()];
			Object[] values = new Object[refBeanList.size()];
			for (int i = 0; i< refBeanList.size() ; i++) {
				RefrenceBean refBean = refBeanList.get(i);
				Object v = props.get(refBean.getFieldName());
				if(null == v || "".equals(v)){
					throw new IllegalArgumentException("Refrence the Object's primary key value cannot be empty");
				}
				names[i] = refBean.getMapping();
				values[i] = v;
			}
			CglibUtil.setPropertyValues(bean, names, values);
			return bean;
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}
	
}
