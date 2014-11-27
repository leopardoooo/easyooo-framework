/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache;

import java.util.List;

/**
 * 分组数据过滤器接口定义
 *
 * @author Killer
 */
public interface DataFilter<E> {
	
	
	/**
	 * 实现该方法完成数据过滤的规则
	 * 
	 * @param groups
	 * @return
	 */
	List<E> doFilter(List<E> groups);
	
}
