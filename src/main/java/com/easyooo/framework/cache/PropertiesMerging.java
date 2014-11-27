/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache;

import java.util.List;
import java.util.Map;

/**
 *
 * 通过缓存的<code>Merging</code>，所查询出来的属性进行合并，
 * 具体合并规则通过该类进行实现
 *
 * @author Killer
 */
public interface PropertiesMerging {
	
	
	/**
	 * 实现该方法完成合并的规则
	 * @param props
	 * @return DTO
	 */
	Object merge(Object dto,List<Map<String, Object>> props)throws CacheException;
	

}
