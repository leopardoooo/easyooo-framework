package com.easyooo.framework.support.transaction;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.Assert;


/**
 * 
 * 事务资源管理器
 * 
 * @see TransactionResource
 *
 * @author Killer
 */
public abstract class TransactionResourceManager {
	
	static final Logger logger = LoggerFactory.getLogger(TransactionResourceManager.class);

	private static final ThreadLocal<LinkedHashMap<Object, TransactionResource>> resources =
			new NamedThreadLocal<LinkedHashMap<Object, TransactionResource>>("transaction resources");


	public static boolean isSynchronizationActive() {
		return (resources.get() != null);
	}
	
	public static void initSynchronization() {
		if (isSynchronizationActive()) {
			throw new IllegalStateException("Cannot activate Transaction resource - already active");
		}
		if(logger.isDebugEnabled()){
			logger.debug("Initializing Transaction resource.");
		}
		resources.set(new LinkedHashMap<Object, TransactionResource>());
	}
	
	public static Map<Object, TransactionResource> getResourceMap() {
		if (!isSynchronizationActive()) {
			throw new IllegalStateException("The Transaction resource is not active");
		}
		Map<Object, TransactionResource> map = resources.get();
		return (Map<Object, TransactionResource>) Collections.unmodifiableMap(map);
	}

	public static boolean hasResource(Object key) {
		Object value = doGetResource(key);
		return (value != null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getResource(Object key) {
		return (T)doGetResource(key);
	}

	private static TransactionResource doGetResource(Object actualKey) {
		Map<Object, TransactionResource> map = resources.get();
		if (map == null) {
			return null;
		}
		return map.get(actualKey);
	}
	
	public static void bindResource(Object key, TransactionResource value){
		Assert.notNull(value, "Value must not be null");
		LinkedHashMap<Object, TransactionResource> map = resources.get();
		// set ThreadLocal Map if none found
		if (map == null) {
			throw new IllegalStateException("The Transaction resource is not active");
		}
		map.put(key, value);
		if (logger.isDebugEnabled()) {
			logger.debug("Bound value [" + value + "] for key [" + key + "] to thread [" +
					Thread.currentThread().getName() + "]");
		}
	}
	
	public static Object unbindResource(Object key){
		Map<Object, TransactionResource> map = resources.get();
		if (map == null) {
			throw new IllegalStateException("The Transaction resource is not active");
		}
		Object value = map.remove(key);
		// Remove entire ThreadLocal if empty...
		if (map.isEmpty()) {
			resources.remove();
		}
		if (value != null && logger.isTraceEnabled()) {
			logger.trace("Removed value [" + value + "] for key [" + key + "] from thread [" +
					Thread.currentThread().getName() + "]");
		}
		return value;
	}
	
	public static void clear() {
		if (!isSynchronizationActive()) {
			throw new IllegalStateException("Cannot deactivate transaction resource - not active");
		}
		
		resources.remove();
		if (logger.isDebugEnabled()) {
			logger.debug("Thread["+ Thread.currentThread().getId() +"] transactional resource has been cleared.");
		}
	}
}
