package com.easyooo.framework.sharding.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解的用途主要配置分库对应的表名，通常标注在Mapper或DAO Class定义上
 * 该注解支持作用于被标注目标的子类
 * 
 * @author Killer
 */
@Target({ ElementType.TYPE })  
@Retention(RetentionPolicy.RUNTIME)  
@Inherited  
@Documented
public @interface Table {
	
	
	/**
	 * 表名，大小写不敏感
	 * 
	 * @return
	 */
	String value();
	
	
	/**
	 * 切分所使用的属性名称，暂无用途，保留元属性
	 * 
	 * @return
	 */
	String shardBy() default "";
	
}
