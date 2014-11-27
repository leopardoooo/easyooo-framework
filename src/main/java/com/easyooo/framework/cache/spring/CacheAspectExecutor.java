/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.spring;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import com.easyooo.framework.cache.CacheManager;
import com.easyooo.framework.cache.DataProxyException;
import com.easyooo.framework.cache.Delegater;
import com.easyooo.framework.cache.annotations.Cache;
import com.easyooo.framework.cache.annotations.GroupStrategy;
import com.easyooo.framework.cache.annotations.MergingStrategy;
import com.easyooo.framework.cache.config.Configuration;
import com.easyooo.framework.cache.impl.CacheChainBuilder;
import com.easyooo.framework.cache.impl.CascadeCacheManager;
import com.easyooo.framework.cache.impl.DefaultDataProxy;
import com.easyooo.framework.common.util.ClassUtil;

/**
 * Aspect Executor Wrapper for Spring Aspect
 * 
 * @see DefaultDataProxy
 * 
 * @author Killer
 */
public class CacheAspectExecutor implements InitializingBean {

	Logger log = LoggerFactory.getLogger(CacheAspectExecutor.class);

	/**
	 * 需要注入的参数
	 */
	private CacheChainBuilder chainBuilder;

	/**
	 * 自动实例化的参数
	 * 
	 * @see {@link CacheAspectExecutor#afterPropertiesSet()}
	 */
	private DefaultDataProxy dataProxy;

	public CacheAspectExecutor() {
	}

	public CacheAspectExecutor(CacheChainBuilder chainBuilder) {
		this.chainBuilder = chainBuilder;
	}

	public Object insert(final ProceedingJoinPoint pjp, Object bean)
			throws Throwable {
		if(!checkHasCache(bean)){
			return pjp.proceed();
		}
		return dataProxy.insert(bean, new DefaultDelegater<Integer>(pjp));
	}

	public Object updateByPrimaryKey(final ProceedingJoinPoint pjp, Object bean)
			throws Throwable {
		if(!checkHasCache(bean)){
			return pjp.proceed();
		}
		return dataProxy.updateByPrimaryKey(bean,
				new DefaultDelegater<Integer>(pjp));
	}
	
	public Object updateByPrimaryKeySelective(final ProceedingJoinPoint pjp, Object bean)
			throws Throwable {
		if(!checkHasCache(bean)){
			return pjp.proceed();
		}
		return dataProxy.updateByPrimaryKeySelective(bean,
				new DefaultDelegater<Integer>(pjp));
	}

	public Object deleteByPrimaryKey(final ProceedingJoinPoint pjp, Object bean)
			throws Throwable {
		if(!checkHasCache(bean)){
			return pjp.proceed();
		}
		return dataProxy.deleteByPrimaryKey(bean,
				new DefaultDelegater<Integer>(pjp));
	}

	public Object selectByPrimaryKey(final ProceedingJoinPoint pjp, Object bean)
			throws Throwable {
		if(!checkHasCache(bean)){
			return pjp.proceed();
		}
		return dataProxy.selectByPrimaryKey(bean, new DefaultDelegater<Object>(
				pjp));
	}
	
	public <T> Object selectByGroupKey(ProceedingJoinPoint pjp, Object bean)
			throws Throwable {
		if(!checkHasCache(bean)){
			return pjp.proceed();
		}
		MethodSignature method = (MethodSignature) pjp.getSignature();
		GroupStrategy group = method.getMethod().getAnnotation(
				GroupStrategy.class);
		if (group == null) {
			return pjp.proceed();
		}
		return dataProxy.selectByGroupKey(bean, group.value(),
				new DefaultDelegater<List<T>>(pjp));
	}
	
	public <T> Object selectAll(ProceedingJoinPoint pjp)
			throws Throwable {
		MethodSignature method = (MethodSignature) pjp.getSignature();
		Class<?> clazz = ClassUtil.getGenericReturnType(method.getMethod(), 0);
		Object bean = clazz.newInstance();
		
		if(!checkHasCache(bean)){
			return pjp.proceed();
		}
		
		return dataProxy.selectByGroupKey(bean, Configuration.DEFAULT_MINI_TABLE,
				new DefaultDelegater<List<T>>(pjp));
	}

	@SuppressWarnings("unchecked")
	public Object selectMergingByPrimaryKey(ProceedingJoinPoint pjp,
			Object bean) throws Throwable {
		if(!checkHasCache(bean)){
			return pjp.proceed();
		}
		MethodSignature method = (MethodSignature) pjp.getSignature();
		MergingStrategy ms = method.getMethod().getAnnotation(
				MergingStrategy.class);
		if (ms == null) {
			return pjp.proceed();
		}
		Class<Object> dtoClass = method.getReturnType();
		return dataProxy.selectMergingByPrimaryKey(bean,
				new DefaultDelegater<Object>(pjp), dtoClass);
	}

	protected void debugAspectMethod(ProceedingJoinPoint pjp) {
		if (log.isDebugEnabled()) {
			StringBuffer sb = new StringBuffer();
			sb.append("\n");
			sb.append("target -> " + pjp.getTarget());
			sb.append("\n");
			sb.append("signature -> " + pjp.getSignature());
			sb.append("\n");
			sb.append("argums -> ["
					+ StringUtils.arrayToDelimitedString(pjp.getArgs(), ",")
					+ "]");
			log.debug(sb.toString());
		}
	}
	
	private boolean checkHasCache(Object bean){
		if(bean == null){
			return false;
		}
		Cache cache = bean.getClass().getAnnotation(Cache.class);
		if(cache == null){
			return false;
		}
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		if (chainBuilder == null) {
			throw new IllegalArgumentException("chainBuilder["
					+ CacheChainBuilder.class + "] must be injected.");
		}

		CacheManager cm = new CascadeCacheManager(chainBuilder);
		this.dataProxy = new DefaultDataProxy(cm);
	}

	static class DefaultDelegater<T> implements Delegater<T> {

		private ProceedingJoinPoint pjp;

		public DefaultDelegater(ProceedingJoinPoint pjp) {
			this.pjp = pjp;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T execute() throws DataProxyException {
			try {
				Object o = pjp.proceed();
				if (o != null) {
					return (T) o;
				}
				return null;
			} catch (Throwable e) {
				throw DataProxyException.newIt(e);
			}
		}

	}

	public void setChainBuilder(CacheChainBuilder chainBuilder) {
		this.chainBuilder = chainBuilder;
	}
}
