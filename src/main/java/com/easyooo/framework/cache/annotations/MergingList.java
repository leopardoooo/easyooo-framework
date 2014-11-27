/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当实体类需要多个合并结果时，使用该类代替<code>Merging</code>
 * 这仅仅是<code>Merging</code>的一个包装
 * @author Killer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MergingList {
	
	/**
	 * Mering配置数组
	 * @return
	 */
	Merging [] value();

}
