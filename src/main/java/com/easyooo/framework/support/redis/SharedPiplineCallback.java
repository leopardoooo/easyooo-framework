package com.easyooo.framework.support.redis;

import redis.clients.jedis.ShardedJedisPipeline;




/**
 * 
 * callback for redis sharedpipline
 * 
 * @author Killer
 */
public interface SharedPiplineCallback{
	
	void doCallback(ShardedJedisPipeline pipeline);
	
}
