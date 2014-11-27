package com.easyooo.framework.support.redis.shard;

import static org.springframework.util.Assert.notNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import com.easyooo.framework.common.net.HostAndPort;

/**
 * 
 * Redis Client for multiple servers,
 * 
 * 所有来自GenericObjectPoolConfig都可以通过setter method来设置
 * 
 * @see GenericObjectPoolConfig
 *
 * @author Killer
 */
public class ShardedJedisClientFactoryBean extends GenericObjectPoolConfig 
	implements FactoryBean<ShardedJedisPool>, InitializingBean, DisposableBean {

	private ShardedJedisPool shardedJedisPool;
	
	private String connectionString;
	
	@Override
	public void destroy() throws Exception {
		if(shardedJedisPool != null){
			shardedJedisPool.destroy();
		}
	}

	@Override
	public ShardedJedisPool getObject() throws Exception {
		return shardedJedisPool;
	}

	@Override
	public Class<?> getObjectType() {
		return ShardedJedisPool.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		notNull(connectionString, "Property 'connectionString' is required");
		List<HostAndPort> haps = HostAndPort.fromStringArray(connectionString);
		List<JedisShardInfo> jsi = new ArrayList<>();
		for (HostAndPort hap : haps) {
			jsi.add(new JedisShardInfo(hap.getHost(), hap.getPort()));
		}
		shardedJedisPool = new ShardedJedisPool(this, jsi);
	}

	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}
	
}
