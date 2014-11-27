/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.common.util;

/**
 * 属性过滤器接口定义
 * 
 * @author Killer
 */
public interface PropertyFilter<T> {
	boolean isFilter(T t);
}
