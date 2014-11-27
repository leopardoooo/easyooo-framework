package com.easyooo.framework.support.redis;

import redis.clients.jedis.JedisCommands;

/**
 * @param V 回调函数的返回值
 * @author Killer
 */
public interface RedisCallback<V> extends Callback<JedisCommands,V>{
	
}
