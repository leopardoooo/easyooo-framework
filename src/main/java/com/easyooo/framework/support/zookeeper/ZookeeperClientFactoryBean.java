package com.easyooo.framework.support.zookeeper;

import static org.springframework.util.Assert.notNull;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Zookeeper client factory bean，它是单实例的，
 * 意味着程序只有一个zk client的实例
 * 
 * @author Killer
 */
public class ZookeeperClientFactoryBean implements
		FactoryBean<CuratorFramework>, InitializingBean, DisposableBean {

	private Logger logger = LoggerFactory.getLogger(getClass());
	// 默认15毫秒连接超时时间
	private static final Integer DEFAULT_CONNECTION_TIMEOUT = 15 * 1000;
	// session有效期60ms
	private static final Integer DEFAULT_SESSION_TIMEOUT = 60 * 1000;
	
	private CuratorFramework zkClient;

	/**
	 * Zookeeper 连接字符串， 这是需要注入的！
	 */
	private String connectionString;
	
	private Integer connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
	
	private Integer sessionTimeout = DEFAULT_SESSION_TIMEOUT;
	
	private Integer maxRetries = 5;
	
	private int baseSleepTimeMs = 1000;

	@Override
	public void afterPropertiesSet() {
		notNull(connectionString, "Property 'connectionString' is required");
		
		this.zkClient = createZookeeperInstance();
		this.zkClient.start();
		
		if(this.zkClient.getState() == CuratorFrameworkState.STARTED){
			logger.info("ZK connection is successful.");
		}else{
			logger.error("ZK connection is unsuccessful.");
		}
	}
	
	@Override
	public CuratorFramework getObject() {
		return zkClient;
	}

	@Override
	public Class<?> getObjectType() {
		return CuratorFramework.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void destroy() throws Exception {
		if(zkClient != null 
				&& zkClient.getState() != CuratorFrameworkState.STOPPED){
			zkClient.close();
		}
	}

	public CuratorFramework createZookeeperInstance() {
		// 重试策略
		RetryPolicy rp = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
		
		// create ZK Client
		CuratorFramework cf =  CuratorFrameworkFactory.builder()
				.connectString(connectionString)
				.retryPolicy(rp)
				.connectionTimeoutMs(connectionTimeout)
				.sessionTimeoutMs(sessionTimeout)
				.build();
		
		return registListener(cf);
	}

	/**
	 * 注册Zookeeper客户端连接状态及异常的日志监听器
	 * 
	 * @param client
	 * @return
	 */
	private CuratorFramework registListener(CuratorFramework client) {
		LogConnectionStateListener lc = new LogConnectionStateListener();
		client.getConnectionStateListenable().addListener(lc);
		client.getUnhandledErrorListenable().addListener(lc);
		return client;
	}

	public void setZkClient(CuratorFramework zkClient) {
		this.zkClient = zkClient;
	}

	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}

	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setSessionTimeout(Integer sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public void setMaxRetries(Integer maxRetries) {
		this.maxRetries = maxRetries;
	}

	public void setBaseSleepTimeMs(int baseSleepTimeMs) {
		this.baseSleepTimeMs = baseSleepTimeMs;
	}
}
