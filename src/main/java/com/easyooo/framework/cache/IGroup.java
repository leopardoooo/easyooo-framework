/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache;

/**
 * <p>
 * 实体类可以实现该接口完成获取组名的属性值，作用同<code>IKeyValue</code>一致
 * </p>
 * 
 * @see IKeyValue
 * 
 * @author Killer
 */
public interface IGroup {

	
	/**
	 * 返回的数组应该和配置的@Group注解的顺序是一致的，否则生成缓存KEY会造成错乱
	 * @param groupName
	 * @return
	 */
	String[] getGroupValues(String groupName);
}
