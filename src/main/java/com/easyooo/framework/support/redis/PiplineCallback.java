package com.easyooo.framework.support.redis;

import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ShardedJedisPipeline;




/**
 * 
 * callback for redis pipline
 * 
 * @author Killer
 */
public interface PiplineCallback{
	
	void doCallback(Pipeline pipeline);
	
	void doCallback(ShardedJedisPipeline pipeline);
	
}
