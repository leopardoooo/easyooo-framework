package com.easyooo.framework.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Transaction;

/**
 * Jedis API Test
 * 
 * simple api, transactions, Pipelining, Sharding
 * @author Killer
 */
@SuppressWarnings("unused")
public class JedisApiTest {
	Logger logger = LoggerFactory.getLogger(getClass());
	final int COUNTER = 12000;
	
	/**
	 * 最简单的调用方式，
	 * 本地1秒钟可设置10000 - 12000次
	 */
	@Test
	@Ignore
	public void testNormalApi(){
		List<String> keys = new ArrayList<String>();
		Jedis jedis = new Jedis("localhost");
		long start = System.currentTimeMillis();
		for(int i = 0; i< COUNTER; i++){
			keys.add("n" + i);
			String result = jedis.set("n" + i, "n" + i);
		}
		long end = System.currentTimeMillis();
		logger.info("SET: " + ((end - start) / 1000.0) + " seconds");
		
		start = System.currentTimeMillis();
		List<String> values = jedis.mget(keys.toArray(new String[]{}));
		end = System.currentTimeMillis();
		
		logger.info("MGet: " + ((end - start) / 1000.0) + " seconds");
		jedis.close();
	}
	
	/**
	 * 一个client发起的事务中的命令可以连续的执行，而中间不会插入其他client的命令。
	 */
	@Test
	@Ignore
	public void testTransactions(){
		Jedis jedis = new Jedis("localhost");
		long start = System.currentTimeMillis();
		Transaction tx = jedis.multi();
		
		for(int i = 0; i< COUNTER; i++){
			tx.set("t" + i, "t" + i);
			// 提示使用JedisTransaction代替
			if(i == 100){
				System.out.println(jedis.get("t1"));
				
				// 无法读取到，是正确的
				//System.out.println(new Jedis("localhost").get("t1"));
			}
		}
		List<Object> results = tx.exec();
		long end = System.currentTimeMillis();
		logger.info("Tx Set: " + ((end - start)/1000.0) + " seconds");
		jedis.close();
		System.out.println("results: " + results);
	}
	
	
	/**
	 * Pipelining
	 * 测试时间：0.287 seconds
	 */
	@Test
	@Ignore
	public void testPipelining(){
		Jedis jedis = new Jedis("localhost");
		Pipeline pipeline = jedis.pipelined();
		long start = System.currentTimeMillis();
		for(int i = 0; i< COUNTER; i++){
			pipeline.set("p" + i, "p" + i);
			if(i == 100){
				System.out.println(jedis.get("p1"));
			}
		}
		List<Object> results = pipeline.syncAndReturnAll();
		long end = System.currentTimeMillis();
		logger.info("Pipelined SET: " + ((end - start)/1000.0) + " seconds");
		jedis.close();
		System.out.println("result: " + results.get(0));
		System.out.println(jedis.get("p1"));
	}
	
	/**
	 * 单机模拟分库
	 * 耗时：1.244 seconds
	 */
	@Test
	@Ignore
	public void testShardNormal(){
		 List<JedisShardInfo> shards = Arrays.asList(
		            new JedisShardInfo("localhost",6379),
		            new JedisShardInfo("localhost",6379));

	    ShardedJedis sharding = new ShardedJedis(shards);

	    long start = System.currentTimeMillis();
	    for (int i = 0; i < COUNTER; i++) {
	        String result = sharding.set("sn" + i, "n" + i);
	    }
	    long end = System.currentTimeMillis();
	    System.out.println("Simple@Sharing SET: " + ((end - start)/1000.0) + " seconds");

	    sharding.disconnect();
	}
	
	/**
	 * 分布式直连异步调用
	 * 耗时：
	 * 0.866 seconds
	 * 0.892 seconds
	 */
	@Test
	@Ignore
	public void testShardpipelined() {
	    List<JedisShardInfo> shards = Arrays.asList(
	            new JedisShardInfo("localhost",6379),
	            new JedisShardInfo("localhost",6379));

	    ShardedJedis sharding = new ShardedJedis(shards);

	    ShardedJedisPipeline pipeline = sharding.pipelined();
	    long start = System.currentTimeMillis();
	    for (int i = 0; i < 100000; i++) {
	        pipeline.set("sp" + i, "p" + i);
	    }
	    List<Object> results = pipeline.syncAndReturnAll();
	    long end = System.currentTimeMillis();
	    System.out.println("Pipelined@Sharing SET: " + ((end - start)/1000.0) + " seconds");

	    sharding.disconnect();
	}
	
	/**
	 * 分布式连接池同步调用
	 * 1.288 seconds
	 * 1.291 seconds
	 */
	@Test
	public void testShardSimplePool() {
		List<JedisShardInfo> shards = Arrays.asList(new JedisShardInfo(
				"localhost", 6379), new JedisShardInfo("localhost", 6379));

		ShardedJedisPool pool = new ShardedJedisPool(new JedisPoolConfig(), shards);
		ShardedJedis one = pool.getResource();

		long start = System.currentTimeMillis();
		for (int i = 0; i < COUNTER; i++) {
			String result = one.set("spn" + i, "n" + i);
		}
		long end = System.currentTimeMillis();
		pool.returnResource(one);
		logger.info("Simple@Pool SET: " + ((end - start) / 1000.0) + " seconds");

		pool.destroy();
	}
	
	/**
	 * 分布式连接池异步调用
	 * 0.452 seconds
	 * 0.43 seconds
	 */
	@Test
	@Ignore
	public void testShardPipelinedPool() {
	    List<JedisShardInfo> shards = Arrays.asList(
	            new JedisShardInfo("localhost",6379),
	            new JedisShardInfo("localhost",6379));

	    ShardedJedisPool pool = new ShardedJedisPool(new JedisPoolConfig(), shards);
	    ShardedJedis one = pool.getResource();
	    ShardedJedisPipeline pipeline = one.pipelined();
	    
	    long start = System.currentTimeMillis();
	    for (int i = 0; i < COUNTER; i++) {
	        pipeline.set("sppn" + i, "n" + i);
	    }
	    List<Object> results = pipeline.syncAndReturnAll();
	    long end = System.currentTimeMillis();
	    pool.returnResource(one);
	    logger.info("Pipelined@Pool SET: " + ((end - start)/1000.0) + " seconds");
	    pool.destroy();
	}


}
