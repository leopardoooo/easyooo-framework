package com.easyooo.framework.support.redis.shard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;

import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;

import com.easyooo.framework.common.util.MapUtil;
import com.easyooo.framework.support.redis.AbstractRedisOperation;
import com.easyooo.framework.support.redis.PiplineCallback;
import com.easyooo.framework.support.redis.PiplineCallbackAdapter;
import com.easyooo.framework.support.redis.RedisCallback;
import com.easyooo.framework.support.redis.TransactionCallback;

/**
 *
 * @author Killer
 */
public class ShardedJedisOperation extends AbstractRedisOperation 
			implements BeanFactoryAware, InitializingBean {

	static final String DEFAULT_SHARDED_JEDIS_FACTORY_BEAN_KEY = "shardedJedisPool";
	
	private String shardedJedisFactoryBeanKey = DEFAULT_SHARDED_JEDIS_FACTORY_BEAN_KEY;
	
	private ShardedJedisPool shardedJedisPool;
	
	private BeanFactory beanFactory;

	@Override
	public List<String> gets(final String... keys) {
		return exec0(new ShardedJedisCallback<List<String>>() {
			@Override
			public List<String> doCallback(ShardedJedis jedis) {
				List<String> response = new ArrayList<String>();
				for (String key : keys) {
					response.add(jedis.get(key));
				}
				return response;
			}
		});
	}

	@Override
	public String sets(final String... keyvalues) {
		return exec0(new ShardedJedisCallback<String>() {
			@Override
			public String doCallback(ShardedJedis jedis) {
				Object[] args = Arrays.asList(keyvalues).toArray();
				final Map<String, Object> map = MapUtil.gmap(args);
				pipelined(new PiplineCallbackAdapter() {
					@Override
					public void doCallback(ShardedJedisPipeline pipeline) {
						for (Entry<String, Object> entry : map.entrySet()) {
							pipeline.set(entry.getKey(), entry.getValue().toString());
						}
					}
				});
				return new String();
			}
		});
	}
	
	@Override
	public Long del(final String... keys) {
		return exec0(new ShardedJedisCallback<Long>() {
			@Override
			public Long doCallback(ShardedJedis jedis) {
				pipelined(new PiplineCallbackAdapter() {
					@Override
					public void doCallback(ShardedJedisPipeline pipeline) {
						for (String key : keys) {
							pipeline.del(key);
						}
					}
				});
				return new Long(keys.length);
			}
		});
	}
	
	@Override
	public List<Object> transaction(TransactionCallback callback) {
		throw new UnsupportedOperationException("Sharded jedis doesn't support transactions");
	}

	@Override
	public void pipelined(final PiplineCallback callback) {
		exec0(new ShardedJedisCallback<Void>() {
			@Override
			public Void doCallback(ShardedJedis jedis) {
				ShardedJedisPipeline sjp = jedis.pipelined();
				callback.doCallback(sjp); 
				sjp.sync();
				return null;
			}
		});
	}
	
	protected <T> T exec0(final ShardedJedisCallback<T> callback){
		return exec(new RedisCallback<T>() {
			@Override
			public T doCallback(JedisCommands param) {
				return callback.doCallback((ShardedJedis)param);
			}
		});
	}
	
	@Override
	public <T> T exec(final RedisCallback<T> callback){
		T t = null;
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			t = callback.doCallback(jedis);
		}finally{
			if(jedis != null){
				shardedJedisPool.returnResource(jedis);
			}
		}
		// handleTheReponseText(text);
		return t;
	}
	
	private interface ShardedJedisCallback<V>{
		V doCallback(ShardedJedis jedis);
	}
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try{
			this.shardedJedisPool =  beanFactory.getBean(shardedJedisFactoryBeanKey, ShardedJedisPool.class);
		}catch(BeansException be){
			logger.error("["+ shardedJedisFactoryBeanKey +"] not in the spring container.");
			throw be;
		}
	}

	public void setShardedJedisFactoryBeanKey(String shardedJedisFactoryBeanKey) {
		this.shardedJedisFactoryBeanKey = shardedJedisFactoryBeanKey;
	}
}
