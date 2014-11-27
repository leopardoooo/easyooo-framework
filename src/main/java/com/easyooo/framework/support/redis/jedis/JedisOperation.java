package com.easyooo.framework.support.redis.jedis;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

import com.easyooo.framework.support.redis.AbstractRedisOperation;
import com.easyooo.framework.support.redis.PiplineCallback;
import com.easyooo.framework.support.redis.RedisCallback;
import com.easyooo.framework.support.redis.TransactionCallback;

/**
 * @author Killer
 */
public class JedisOperation extends AbstractRedisOperation implements InitializingBean, BeanFactoryAware {
	
	static final String DEFAULT_JEDIS_FACTORY_BEAN_KEY = "jedisPool";
	
	private String jedisFactoryBeanKey = DEFAULT_JEDIS_FACTORY_BEAN_KEY;
	
	private JedisPool jedisPool;
	
	private BeanFactory beanFactory;
	
	@Override
	public List<String> gets(final String... keys) {
		return exec0(new JedisCallback<List<String>>() {
			@Override
			public List<String> doCallback(Jedis jedis) {
				return jedis.mget(keys);
			}
		});
	}

	@Override
	public String sets(final String... keyvalues) {
		return exec0(new JedisCallback<String>() {
			@Override
			public String doCallback(Jedis jedis) {
				return jedis.mset(keyvalues);
			}
		});
	}

	@Override
	public Long del(final String... keys) {
		return exec0(new JedisCallback<Long>() {
			@Override
			public Long doCallback(Jedis jedis) {
				return jedis.del(keys);
			}
		});
	}
	
	@Override
	public List<Object> transaction(final TransactionCallback callback){
		return exec0(new JedisCallback<List<Object>>(){
			@Override
			public List<Object> doCallback(Jedis jedis) {
				Transaction trans = jedis.multi();
				callback.doCallback(trans);
				return trans.exec();
			}
		});
	}
	
	public void pipelined(final PiplineCallback callback){
		exec0(new JedisCallback<Void>(){
			@Override
			public Void doCallback(Jedis jedis) {
				Pipeline pipeline = jedis.pipelined();
				callback.doCallback(pipeline);
				pipeline.sync();
				return null;
			}
		});
	}
	
	protected <T> T exec0(final JedisCallback<T> callback){
		return exec(new RedisCallback<T>() {
			@Override
			public T doCallback(JedisCommands param) {
				return callback.doCallback((Jedis)param);
			}
		});
	}
	
	@Override
	public <T> T exec(final RedisCallback<T> callback){
		T t = null;
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			t = callback.doCallback(jedis);
		}finally{
			if(jedis != null){
				jedisPool.returnResource(jedis);
			}
		}
		// handleTheReponseText(text);
		return t;
	}

	private interface JedisCallback<V>{
		V doCallback(Jedis jedis);
	}
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try{
			this.jedisPool =  beanFactory.getBean(jedisFactoryBeanKey, JedisPool.class);
		}catch(BeansException be){
			logger.error("["+ jedisFactoryBeanKey +"] not in the spring container.");
			throw be;
		}
	}

	public void setJedisFactoryBeanKey(String jedisFactoryBeanKey) {
		this.jedisFactoryBeanKey = jedisFactoryBeanKey;
	}
	
	public void setJedisPool(JedisPool jedisPool){
		this.jedisPool = jedisPool;
	}
}

