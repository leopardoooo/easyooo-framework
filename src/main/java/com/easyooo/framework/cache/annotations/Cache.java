/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.easyooo.framework.cache.CacheLevel;
import com.easyooo.framework.cache.KeyBuilder;
import com.easyooo.framework.cache.impl.DefaultKeyBuilder;

/**
 * 
 * 被标注的类必须实现PropertyMapper接口，
 * 如果没有实现接口默认采用反射机制将结果集映射到实体Bean
 * 
 * @see com.yaochen.boss.data.cache.boservice.common.cache.core.PropertyMapper
 * 
 * @author Killer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Cache {
	
	/**
	 * This is the primary key of prefix cache
	 * default is class name
	 */
	String value() default "";
	
	
	CacheLevel level() default CacheLevel.REDIS;
	
	
	Class<? extends KeyBuilder> keyBuilder() default DefaultKeyBuilder.class;
	
}
