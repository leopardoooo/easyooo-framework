package com.easyooo.framework.redis;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.JedisPool;

import com.easyooo.framework.support.redis.RedisTemplate;
import com.easyooo.framework.support.redis.jedis.JedisOperation;

/**
 *
 * @author Killer
 */
public class SortedSetTest {

	static JedisPool pool;
	static RedisTemplate redisTemplate;
	
	@BeforeClass
	public static void setUp(){
		pool = new JedisPool("192.168.1.206", 6379);
		JedisOperation opt = new JedisOperation();
		opt.setJedisPool(pool);
		
		redisTemplate = new RedisTemplate();
		redisTemplate.setRedisOperation(opt);
	}
	
	@AfterClass
	public static void destory(){
		pool.destroy();
	}
	
	@Test
	public void testExpire()throws Exception{
		redisTemplate.zadd("zadd", 1.0, "a");
		redisTemplate.zadd("zadd", 3.0, "c");
		redisTemplate.zadd("zadd", 2.0, "b");
		Set<String> rs = redisTemplate.zrangeByIndex("zadd", 0, -1);
		assertThat(rs, hasSize(3));
		Long count = redisTemplate.zcount("zadd", 1.0, 3.0);
		assertThat(count, is(3L));
		
		Long rank = redisTemplate.zrank("zadd", "a");
		assertThat(rank, is(0L));
		
		Double dl = redisTemplate.zincrby("zadd", 4.0, "a");
		assertThat(dl, is(5.0));
		
		redisTemplate.del("zadd");
	}
}
