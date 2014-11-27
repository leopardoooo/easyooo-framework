package com.easyooo.framework.redis;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import redis.clients.jedis.JedisPool;

import com.easyooo.framework.support.redis.RedisTemplate;
import com.easyooo.framework.support.redis.jedis.JedisOperation;

/**
 * Redis Template Unit Test Case
 *
 * @author Killer
 */
public class RedisTemplateTest {
	
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
	@Ignore
	public void testExpire()throws Exception{
		redisTemplate.set("aaa", "111");
		redisTemplate.expire("aaa", 3);
		assertThat(redisTemplate.get("aaa"), is("111"));
		Thread.sleep(4 * 1000L);
		assertThat(redisTemplate.get("aaa"), nullValue());
	}
	
	@Test
	@Ignore
	public void testPersist()throws Exception{
		redisTemplate.set("bbb", 3, "222");
		Thread.sleep(4 * 1000L);
		assertThat(redisTemplate.get("bbb"), nullValue());
		
		redisTemplate.set("bbb", 3, "222");
		redisTemplate.persist("bbb");
		Thread.sleep(4 * 1000L);
		assertThat(redisTemplate.get("bbb"), is("222"));
		redisTemplate.expire("bbb", 3);
	}
	
	@Test
	public void testSmembers()throws Exception{
		System.out.println(redisTemplate.exists("a"));
	}
}
