/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 主键属性，作用于属性上
 * @author Killer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface KeyProperty {

	/**
	 * 如果某个实体类存在复合主键时，可使用该字段进行排序
	 * 所影响的是主键-值部分的顺序
	 */
	int value() default -1;
	
}
