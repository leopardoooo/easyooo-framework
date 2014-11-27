package com.easyooo.framework.rule.impl;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easyooo.framework.rule.Language;
import com.easyooo.framework.rule.Rule;
import com.easyooo.framework.rule.RuleClassLoader;
import com.easyooo.framework.rule.RuleException;
import com.easyooo.framework.rule.RuleExecutor;
import com.easyooo.framework.rule.groovy.GroovyRuleClassLoader;
import com.easyooo.framework.rule.java.JavaRuleClassLoader;

/**
 * Rule 类管理器
 * 
 * @author Killer
 */
public class RuleClassManager {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 缓存规则及编译好的规则实例
	 * Key: rule id
	 * Value: {@link CacheValue}
	 */
	private final ConcurrentHashMap<String, Future<CacheValue>> cache 
							= new ConcurrentHashMap<>();
	
	private static RuleClassManager instance;

	private RuleClassManager(){
		super();
	}
	
	public static synchronized RuleClassManager getInstance() {
		if (instance == null) {
			return (instance = new RuleClassManager());
		}
		return instance;
	}
	
	/**
	 * 获取一个规则实现，如果不存在，则动态编译成Class
	 */
	public RuleExecutor get(final Rule ruleObj)throws RuleException {
		if(ruleObj == null) 
			return null;
		Future<CacheValue> future = cache.get(ruleObj.getRuleId());
		if(future == null){
			Callable<CacheValue> eval = new Callable<CacheValue>() {
				@Override
				public CacheValue call() throws Exception {
					try {
						RuleExecutor executor = loadRuleInstance(ruleObj);
						return new CacheValue(ruleObj, executor);
					} catch (RuleException e) {
						throw new ExecutionException(e);
					}
				}
			};
			
			FutureTask<CacheValue> ft = new FutureTask<CacheValue>(eval);
			future = cache.putIfAbsent(ruleObj.getRuleId(), ft);
			if(future == null){
				future = ft;
				ft.run();
			}
		}
		
		try {
			CacheValue obj = future.get();
			
			if(obj.rule.getVersion().equals(ruleObj.getVersion())){
				return obj.executor;
			}else{
				// 版本不一致，删除老版本，编译新版本
				logger.debug(String.format("Check do not agree to release[%d,%d], recompile",
						obj.rule.getVersion(), ruleObj.getVersion()));
				uninstallRule(obj.rule);
				
				// 编译新的内容并设置到缓存
				return get(ruleObj);
			}
		} catch (InterruptedException e) {
			cache.remove(ruleObj.getRuleId());
			throw new RuleException(e);
		} catch (ExecutionException e) {
			cache.remove(ruleObj.getRuleId());
			throw new RuleException("Load " + ruleObj.getRuleId() + " error", e);
		}
	}
	
	public RuleExecutor getIfExist(final Rule ruleObj)throws RuleException{
		Future<CacheValue> future = cache.get(ruleObj.getRuleId());
		if(null == future){
			return null;
		}
		if(future.isDone()){
			try {
				return future.get().executor;
			} catch (InterruptedException e) {
				throw new RuleException(e);
			} catch (ExecutionException igore) {
			}
		}
		return null;
	}
	
	/**
	 * 卸载一个Rule，该方法一般不会主动调用
	 */
	public boolean uninstallRule(final Rule ruleObj)throws RuleException{
		cache.remove(ruleObj.getRuleId());
		try {
			return new ScriptWriter(ruleObj).deleteClass();
		} catch (IOException e) {
			throw new RuleException(e);
		}
	}
	
	private RuleExecutor loadRuleInstance(Rule rule)throws RuleException{
		ScriptWriter sw = new ScriptWriter(rule);
		Class<?> clazz = sw.getScriptClass();
		if(clazz == null){
			RuleClassLoader rcl = switchClassLoader(rule.getLanguage());
			if(rcl == null){
				throw new RuleException("Can not find a matching loader");
			}
			clazz = rcl.loadClass(rule);
		}
		RuleExecutor re = null;
		try {
			re = (RuleExecutor)clazz.newInstance();
		} catch (Exception e) {
			throw new RuleException("Instantiate the script error", e);
		}
		
		return re;
	}
	
	private class CacheValue{
		public Rule rule;
		public RuleExecutor executor;
		
		public CacheValue(Rule rule, RuleExecutor executor){
			this.rule = rule;
			this.executor = executor;
		}
	}
	
	/**
	 * 目前仅支持两种编译成JVM的字节码加载器，如果需要其它支持则进行扩展，
	 * JVM可以直接内置JS执行引擎，也可以通过命令行的方式编译成class文件，
	 * 但Class内容不好控制，因此这里不适用classLoader的模式。
	 * 
	 * @param type
	 * @return
	 */
	private RuleClassLoader switchClassLoader(Language type){
		switch (type) {
			case GROOVY:
				return new GroovyRuleClassLoader();
			case JAVA:
				return new JavaRuleClassLoader();
			default:
				return null;
		}
	}
	
}
