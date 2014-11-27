package com.easyooo.framework.support.redis.jedis;

import static org.springframework.util.Assert.notNull;

import java.util.List;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import redis.clients.jedis.JedisPool;

import com.easyooo.framework.common.net.HostAndPort;

/**
 * 
 * Redis Client for standalone Server,
 * 
 * 所有来自GenericObjectPoolConfig都可以通过setter method来设置
 * 
 * @see GenericObjectPoolConfig
 *
 * @author Killer
 */
public class JedisClientFactoryBean extends GenericObjectPoolConfig implements FactoryBean<JedisPool>, InitializingBean, DisposableBean{

	private JedisPool redisClient;
	
	private String connectionString;
	
	@Override
	public JedisPool getObject() throws Exception {
		return redisClient;
	}

	@Override
	public Class<?> getObjectType() {
		return JedisPool.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		notNull(connectionString, "Property 'connectionString' is required");
		List<HostAndPort> haps = HostAndPort.fromStringArray(connectionString);
		if(haps.size() > 1){
			throw new IllegalArgumentException("The class supports only a single server. But '"+ connectionString +"' is cluter,"
					+ "Please refer to the ShardRedisClientFactoryBean.");
		}
		HostAndPort hap = haps.get(0);
		redisClient = new JedisPool(this, hap.getHost(), hap.getPort());
	}
	
	@Override
	public void destroy() throws Exception {
		if(redisClient != null){
			redisClient.destroy();
		}
	}

	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}
}
