/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.common.util;


/**
 * 分组接口定义，
 * 
 * {@link ListUtil#grouping(java.util.List)}
 *
 * @author Killer
 */
public interface Grouping<K, T> {

	
	public K getGroupingKey(T element);
	
}
