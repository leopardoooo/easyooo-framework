/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.impl;

import java.text.MessageFormat;

import org.springframework.util.StringUtils;

import com.easyooo.framework.cache.KeyBuilder;
import com.easyooo.framework.cache.config.CacheBean;

/**
 *  在不指定缓存KEY生成器的情况下，默认会使用该类的实例生成缓存的KEY
 *  
 * @author Killer
 */
public class DefaultKeyBuilder implements KeyBuilder {
	
	private final String SPLIT_CHAR = ",";
	
	private final String KEY_FORMAT1 = "{0}({1})";
	
	private final String KEY_FORMAT2 = "{0}({1})#{2}";
	
	private final String KEY_FORMAT3 = "all({0})";

	
	/**
	 * 根据primaryKeys参数的数量，所生成的KEY格式 <b><i>类名(主键值1,主键值2,...)</i></b>
	 * <p>如：CCust(张三,360124190006541234)</p>
	 */
	@Override
	public String buildKey(CacheBean cacheBean, String ...primaryKeys) {
		String prefix = cacheBean.getPrefix();
		String keyStr = StringUtils.arrayToDelimitedString(primaryKeys, SPLIT_CHAR);
		return MessageFormat.format(KEY_FORMAT1, prefix, keyStr);
	}

	
	/**
	 * 生成规则和单个实体类的缓存KEY一致，只是在KEY的末尾，增加了后缀字符，支持多个分组，
	 * 就像 "CUser(010112345)#cust_id" 可以看出这是一个默认的单属性分组，分组的别称是属性名称"cust_id"
	 * 还有比如 "CUser(数字电视,123456)#device" 在配置了多属性的分组时，就需要配置分组的别称
	 */
	@Override
	public String buildGroupKey(CacheBean cacheBean, String groupName, String...groups) {
		String prefix = cacheBean.getPrefix();
		String keyStr = StringUtils.arrayToDelimitedString(groups, SPLIT_CHAR);
		return MessageFormat.format(KEY_FORMAT2, prefix, keyStr, groupName);
	}

	@Override
	public String buildMiniTableKey(CacheBean cacheBean) {
		return MessageFormat.format(KEY_FORMAT3, cacheBean.getPrefix());
	}

}
