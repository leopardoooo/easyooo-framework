package com.easyooo.framework.cache.transaction;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easyooo.framework.cache.CacheException;
import com.easyooo.framework.cache.annotations.ThreadSafety;
import com.easyooo.framework.cache.storage.JVMCache;

/**
 * 直接修改JVM缓存，修改时会锁住整个缓存
 * 
 * @author Killer
 */
@ThreadSafety
public class UpdateJvmCacheManager {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private final Map<String, Object> cacheMap = new JVMCache().getCacheObject();
	
	/**
	 * 根据命令直接修改缓存数据
	 */
	public void update(Collection<Command> commands){
		if(commands == null){
			return ;
		}
		synchronized (cacheMap) {
			try {
				new DefaultJvmCacheTransactionCallback(cacheMap, commands).doCallback();
			} catch (CacheException e) {
				logger.error("An error occurred in the update JVMCache" , e);
			}
		}
	}
}
