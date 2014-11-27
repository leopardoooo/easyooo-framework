/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 被修饰为Group的Class,必须实现Group接口
 * 
 * @see com.yaochen.boss.data.cache.boservice.common.cache.core.IGroup
 * 
 * @author Killer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Group {

	/**
	 * @see KeyProperty#value()
	 */
	int value() default -1;
	
	
	/**
	 * 分组名，一个实体Bean可存在多个组
	 * @return
	 */
	String name() default "";
	
}
