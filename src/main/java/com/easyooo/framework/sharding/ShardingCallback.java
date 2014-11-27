package com.easyooo.framework.sharding;

/**
 * @see ShardingContextExecutor
 * 
 * @author Killer
 */
public interface ShardingCallback {
	
	Object doCallback()throws Throwable;

}
