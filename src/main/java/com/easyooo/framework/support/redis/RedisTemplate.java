package com.easyooo.framework.support.redis;

import static org.springframework.util.Assert.notNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;

import redis.clients.jedis.Tuple;

/**
 * Template for Redis
 *
 * @author Killer
 */
public class RedisTemplate implements InitializingBean, RedisOperation{

	private RedisOperation redisOperation;
	
	public String get(final String key) {
		return redisOperation.get(key);
	}

	public List<String> gets(final String... keys) {
		return redisOperation.gets(keys);
	}

	public String sets(final String... keyvalues) {
		return redisOperation.sets(keyvalues);
	}

	public String set(final String key, final int seconds, final String value) {
		return redisOperation.set(key, seconds, value);
	}
	
	public String set(final String key, final String value) {
		return redisOperation.set(key, value);
	}

	public Long incr(final String key) {
		return redisOperation.incr(key);
	}

	public Long incrby(final String key, final Long increment) {
		return redisOperation.incrby(key, increment);
	}
	
	public Long decr(final String key) {
		return redisOperation.decr(key);
	}

	public Long decrby(final String key, final Long decrement) {
		return redisOperation.decrby(key, decrement);
	}
	
	@Override
	public Long del(String... keys) {
		return redisOperation.del(keys);
	}

	@Override
	public boolean exists(String key) {
		return redisOperation.exists(key);
	}

	@Override
	public Long lrem(String key, Long count, String value) {
		return redisOperation.lrem(key, count, value);
	}

	@Override
	public Long rpush(String key, String... values) {
		return redisOperation.rpush(key, values);
	}

	@Override
	public List<String> lrange(String key, Long start, Long stop) {
		return redisOperation.lrange(key, start, stop);
	}

	@Override
	public String lset(String key, Long index, String value) {
		return redisOperation.lset(key, index, value);
	}
	
	public List<Object> transaction(final TransactionCallback callback){
		return redisOperation.transaction(callback);
	}
	
	public void pipelined(final PiplineCallback callback){
		redisOperation.pipelined(callback);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		notNull(redisOperation, "Property 'redisOperation' is required");
	}

	public void setRedisOperation(RedisOperation redisOperation) {
		this.redisOperation = redisOperation;
	}

	@Override
	public String hmset(String key, Map<String, String> fieldValues) {
		return redisOperation.hmset(key, fieldValues);
	}

	@Override
	public Long hincrBy(String key, String field, Long increment) {
		return redisOperation.hincrBy(key, field, increment);
	}

	@Override
	public List<String> hmget(String key, String... fields) {
		return redisOperation.hmget(key, fields);
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		return redisOperation.hgetAll(key);
	}

	@Override
	public Set<String> hkeys(String key) {
		return redisOperation.hkeys(key);
	}

	@Override
	public Long hdel(String key, String... fields) {
		return redisOperation.hdel(key, fields);
	}

	@Override
	public boolean hset(String key, String field, String value) {
		return redisOperation.hset(key, field, value);
	}

	@Override
	public Long sadd(String key, String... members) {
		return redisOperation.sadd(key, members);
	}

	@Override
	public Long scard(String key) {
		return redisOperation.scard(key);
	}

	@Override
	public Set<String> smembers(String key) {
		return redisOperation.smembers(key);
	}

	@Override
	public Long srem(String key, String... values) {
		return redisOperation.srem(key, values);
	}
	
	@Override
	public List<String> srandmember(String key, Integer count) {
		return redisOperation.srandmember(key, count);
	}

	@Override
	public Long expire(String key, Integer seconds) {
		return redisOperation.expire(key, seconds);
	}
	
	@Override
	public Long expireAt(String key, long unixTime) {
		return redisOperation.expireAt(key, unixTime);
	}
	
	@Override
	public Long persist(String key) {
		return redisOperation.persist(key);
	}

	@Override
	public Long ttl(String key) {
		return redisOperation.ttl(key);
	}

	@Override
	public Long zadd(String key, Double score, String member) {
		return redisOperation.zadd(key, score, member);
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMember) {
		return redisOperation.zadd(key, scoreMember);
	}

	@Override
	public Long zrem(String key, String... member) {
		return redisOperation.zrem(key, member);
	}

	@Override
	public Double zincrby(String key, double score, String member) {
		return redisOperation.zincrby(key, score, member);
	}

	@Override
	public Long zrank(String key, String member) {
		return redisOperation.zrank(key, member);
	}

	@Override
	public Long zrevrank(String key, String member) {
		return redisOperation.zrevrank(key, member);
	}

	@Override
	public Long zcount(String key, double min, double max) {
		return redisOperation.zcount(key, min, max);
	}

	@Override
	public Double zscore(String key, String member) {
		return redisOperation.zscore(key, member);
	}

	@Override
	public Set<String> zrangeByIndex(String key, long start, long end) {
		return redisOperation.zrangeByIndex(key, start, end);
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max) {
		return redisOperation.zrangeByScore(key, min, max);
	}

	@Override
	public Set<String> zrevrangeByIndex(String key, long start, long end) {
		return redisOperation.zrevrangeByIndex(key, start, end);
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min) {
		return redisOperation.zrevrangeByScore(key, max, min);
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max,
			int offset, int count) {
		return redisOperation.zrangeByScore(key, min, max, offset, count);
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min,
			int offset, int count) {
		return redisOperation.zrevrangeByScore(key, max, min, offset, count);
	}

	@Override
	public Set<Tuple> zrangeWithScores(String key, long start, long end) {
		return redisOperation.zrangeWithScores(key, start, end);
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
		return redisOperation.zrevrangeWithScores(key, start, end);
	}

	@Override
	public Long lpush(String key, String... values) {
		return redisOperation.lpush(key, values);
	}

	@Override
	public String lpop(String key) {
		return redisOperation.lpop(key);
	}

	@Override
	public String rpop(String key) {
		return redisOperation.rpop(key);
	}

}
