/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 引用其它的实体类，与<code>Merging</code>并用
 * @see Merging
 * @author Killer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Refrence {
	
	/**
	 * refrence class
	 */
	Class<? extends Object> value();
	
	/**
	 * 映射的属性名称，默认与当前属性名称一致
	 * @return
	 */
	String mapping() default "";
}
