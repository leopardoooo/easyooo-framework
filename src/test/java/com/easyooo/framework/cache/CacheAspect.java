/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;

import com.easyooo.framework.cache.spring.CacheAspectExecutor;

/**
 * 通过AOP的方式将缓存与数据层集成
 * 
 * @author Killer
 */
@Aspect
public class CacheAspect implements Ordered {
	
	private CacheAspectExecutor executor;
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Insert Pointcuts & Advice
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Pointcut("execution(* com.easyooo.framework.demo.mapper..*.insert(..)) && args(bean,..)")
	public void insertPointcut(Object bean) {
	}
	@Pointcut("execution(* com.easyooo.framework.demo.mapper..*.insertSelective(..)) && args(bean,..)")
	public void insertSelectivePointcut(Object bean) {
	}
	
	@Around("insertPointcut(bean) || insertSelectivePointcut(bean)")
	public Object insert(ProceedingJoinPoint pjp, Object bean) throws Throwable {
		return executor.insert(pjp, bean);
	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// updateByPrimaryKey Pointcuts & Advice
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Pointcut("execution(* com.easyooo.framework.demo.mapper..*.updateByPrimaryKey(..)) && args(bean,..)")
	public void updateByPrimaryKeyPointcut(Object bean) {
	}
	@Pointcut("execution(* com.easyooo.framework.demo.mapper..*.updateByPrimaryKeySelective(..)) && args(bean,..)")
	public void updateByPrimaryKeySelectivePointcut(Object bean) {
	}
	@Around("updateByPrimaryKeyPointcut(bean)")
	public Object updateByPrimaryKey(ProceedingJoinPoint pjp, Object bean) throws Throwable {
		return executor.updateByPrimaryKey(pjp, bean);
	}
	@Around("updateByPrimaryKeySelectivePointcut(bean)")
	public Object updateByPrimaryKeySelective(ProceedingJoinPoint pjp, Object bean) throws Throwable {
		return executor.updateByPrimaryKeySelective(pjp, bean);
	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// deleteByPrimaryKey Pointcuts & Advice
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Pointcut("execution(* com.easyooo.framework.demo.mapper..*.deleteByPrimaryKey(..)) && args(bean,..)")
	public void deleteByPrimaryKeyPointcut(Object bean) {
	}
	@Around("deleteByPrimaryKeyPointcut(bean)")
	public Object deleteByPrimaryKey(ProceedingJoinPoint pjp, Object bean) throws Throwable {
		return executor.deleteByPrimaryKey(pjp, bean);
	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// selectByPrimaryKey Pointcuts & Advice
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Pointcut("execution(* com.easyooo.framework.demo.mapper..*.selectByPrimaryKey(..)) && args(bean,..)")
	public void selectByPrimaryKeyPointcut(Object bean) {
	}
	@Around("selectByPrimaryKeyPointcut(bean)")
	public Object selectByPrimaryKey(ProceedingJoinPoint pjp, Object bean) throws Throwable {
		return executor.selectByPrimaryKey(pjp, bean);
	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// selectByGroupKey Pointcuts & Advice
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Pointcut("execution(* com.easyooo.framework.demo.mapper..*.select*(..)) && args(bean,..)")
	public void selectByGroupKeyPointcut(Object bean) {
	}
	
	@Around("selectByGroupKeyPointcut(bean)")
	public Object selectByGroupKey(ProceedingJoinPoint pjp, Object bean) throws Throwable {
		return executor.selectByGroupKey(pjp, bean);
	}
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// selectAll Pointcuts & Advice
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Around("execution(* com.easyooo.framework.demo.mapper..*.selectAll())")
	public Object selectAll(ProceedingJoinPoint pjp)throws Throwable {
		return executor.selectAll(pjp);
	}
		
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// selectMergingObjectByPrimaryKey Pointcuts & Advice
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Pointcut("execution(* com.easyooo.framework.demo.mapper..*.select*(..)) && args(bean,..)")
	public void selectMergingByPrimaryKeyPointcut(Object bean) {
	}
	
	@Around("selectMergingByPrimaryKeyPointcut(bean)")
	public Object selectMergingByPrimaryKey(ProceedingJoinPoint pjp, Object bean) throws Throwable {
		return executor.selectMergingByPrimaryKey(pjp, bean);
	}

	@Override
	public int getOrder() {
		return 1;
	}
	
	public void setExecutor(CacheAspectExecutor executor) {
		this.executor = executor;
	}
}
