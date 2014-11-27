package com.easyooo.framework.support.redis;

import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ShardedJedisPipeline;

/**
 *
 * @author Killer
 */
public abstract class PiplineCallbackAdapter implements PiplineCallback {
	@Override
	public void doCallback(Pipeline pipeline) {
		// do nothing
	}
	@Override
	public void doCallback(ShardedJedisPipeline pipeline) {
		// do nothing
	}
}
