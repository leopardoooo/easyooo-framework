package com.easyooo.framework.support.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 *
 * @author Killer
 */
public class CuratorFrameworkExample {

	final String zkConnectionString = "localhost:2181";
	
	public void initialize(){
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.newClient(
				zkConnectionString, retryPolicy);
		client.start();
		
		
		
		try {
			client.create()
				.creatingParentsIfNeeded()
				.forPath("/my/curator", "Hello".getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			client.close();
		}
	}
	
	
	public static void main(String[] args) {
		new CuratorFrameworkExample().initialize();
	}
	
}
