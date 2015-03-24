/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 集合工具类
 * 
 * @author Killer
 */
public abstract class ListUtil {

	/**
	 * 分组函数，可根据规定的分组主键进行分组, 需要实现<code>GroupingKey</code> 接口
	 * 
	 * @see GroupingKey
	 * @param src
	 * @return
	 */
	public static <K, T extends GroupingKey<K>> Map<K, List<T>> grouping(List<T> src){
		Map<K, List<T>> map = new LinkedHashMap<K, List<T>>();
		if(src == null || src.size() == 0){
			return map;
		}
		for (T t : src) {
			K _gk = t.getGroupingKey();
			if(!map.containsKey(_gk)){
				map.put(_gk, new ArrayList<T>());
			}
			
			map.get(_gk).add(t);
		}
		
		return map;
	}
	
	/**
	 * 给传入的集合分组，分组属性由传入的接口参数返回
	 * @return
	 */
	public static <K, T> Map<K, List<T>> grouping(List<T> src, Grouping<K, T> group){
		Map<K, List<T>> map = new LinkedHashMap<K, List<T>>();
		if(src == null || src.size() == 0){
			return map;
		}
		for (T t : src) {
			K _gk = group.getGroupingKey(t);
			if(!map.containsKey(_gk)){
				map.put(_gk, new ArrayList<T>());
			}
			
			map.get(_gk).add(t);
		}
		
		return map;
	}
	
	/**
	 * 将不同元素的集合按主键值转换成Map返回,
	 * list元素应保证唯一性，否则前面的会被覆盖
	 * 
	 * @return
	 */
	public static <K, T> Map<K, T> groupingToEntry(List<T> src, Grouping<K, T> group){
		Map<K, T> map = new LinkedHashMap<>();
		if(src == null || src.size() == 0){
			return map;
		}
		for (T t : src) {
			K k = group.getGroupingKey(t);
			map.put(k, t);
		}
		
		return map;
	}
	
	/**
	 * 获取第一个值
	 * @param list
	 * @return
	 */
	public static <T> T getFirstElement(List<T> list){
		if(list != null && list.size() >= 1){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 属性过滤器
	 * 
	 * @param dataList 数据源
	 * @param pf 过滤器
	 */
	public static <T> List<T> filterBy(List<T> dataList, PropertyFilter<T> pf){
		for (Iterator<T> iterator = dataList.iterator(); iterator.hasNext();) {
			if(pf.isFilter(iterator.next())){
				iterator.remove();
			}
		}
		return dataList;
	}
	
}
