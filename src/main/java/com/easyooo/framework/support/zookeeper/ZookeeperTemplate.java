package com.easyooo.framework.support.zookeeper;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Zookeeper 模板类
 *
 * @author Killer
 */
public class ZookeeperTemplate implements InitializingBean, ZookeeperOperation{
	
	private CuratorFramework zkClient;

	/**
	 * will create the given ZNode with the given data
	 * 
	 * @param path ZK Node Path
	 * @param payload byte data
	 * @throws TemplateException
	 */
	public String create(final String path, final byte[] payload) throws ZookeeperExpcetion{
		return doTemplate(new ZkCallback<String>() {
			@Override
			public String callback() throws Exception {
				return zkClient.create().forPath(path, payload);
			}
		});
    }
	
	/**
	 * create the given EPHEMERAL-SEQUENTIAL ZNode with the given data using Curator protection.
	 * 
	 * @param path ZK Node Path
	 * @param payload byte data
	 * @throws ZookeeperExpcetion
	 */
	public String createEphemeral(final String path, final byte[] payload) throws ZookeeperExpcetion{
		return doTemplate(new ZkCallback<String>() {
			@Override
			public String callback() throws Exception {
				return zkClient.create().withMode(CreateMode.EPHEMERAL)
						.forPath(path, payload);
			}
		});
    }
	
	/**
	 * create the given EPHEMERAL-SEQUENTIAL ZNode with the given data using Curator protection.
	 * @param path ZK Node Path
	 * @param payload byte data
	 * @throws ZookeeperExpcetion
	 */
	public String createEphemeralSequential(final String path, final byte[] payload) throws ZookeeperExpcetion{
		return doTemplate(new ZkCallback<String>() {
			@Override
			public String callback() throws Exception {
				return zkClient.create().withProtection()
						.withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
						.forPath(path, payload);
			}
		});
    }
	
	/**
	 * 读取节点的二进制数据
	 * 
	 * @param path
	 * @return
	 * @throws ZookeeperExpcetion
	 */
	public byte[] readData(final String path)throws ZookeeperExpcetion{
		return doTemplate(new ZkCallback<byte[]>(){
			@Override
			public byte[] callback() throws Exception {
				return zkClient.getData().forPath(path);
			}
		});
	}
	
	/**
	 * set data for the given node
	 * 
	 * @param path ZK Node Path
	 * @param payload byte data
	 * @throws ZookeeperExpcetion
	 */
	public Stat resetData(final String path, final byte[] payload) throws ZookeeperExpcetion{
		return doTemplate(new ZkCallback<Stat>() {
			@Override
			public Stat callback() throws Exception {
				return zkClient.setData().forPath(path, payload);
			}
		});
    }
	
	/**
	 * delete the given node
	 * @param path 
	 * @throws ZookeeperExpcetion
	 */
	public void delete(final String path) throws ZookeeperExpcetion{
		doTemplate(new ZkCallback<Void>() {
			@Override
			public Void callback() throws Exception {
				return zkClient.delete().forPath(path);
			}
		});
    }
	
	/**
	 * Get children nodes
	 * @param path 父节点
	 */
	public List<String> getChildren(final String path)throws ZookeeperExpcetion{
		return doTemplate(new ZkCallback<List<String>>() {
			@Override
			public List<String> callback() throws Exception {
				return zkClient.getChildren().forPath(path);
			}
		});
	}
	
	/**
	 * Get children and set a watcher on the node. 
	 * The watcher notification will come through the
     * CuratorListener (see setDataAsync() above).
	 * @param path
	 * @return
	 * @throws ZookeeperExpcetion
	 */
	public List<String> watchedGetChildren(final String path)throws ZookeeperExpcetion{
		return doTemplate(new ZkCallback<List<String>>() {
			@Override
			public List<String> callback() throws Exception {
				return zkClient.getChildren().watched().forPath(path);
			}
		});
	}
	
	/**
	 * Get children and set the given watcher on the node.
	 * @param path
	 * @param watcher
	 * @return
	 * @throws ZookeeperExpcetion
	 */
	public List<String> watchedGetChildren(final String path, final Watcher watcher)throws ZookeeperExpcetion{
		return doTemplate(new ZkCallback<List<String>>() {
			@Override
			public List<String> callback() throws Exception {
				return zkClient.getChildren().usingWatcher(watcher).forPath(path);
			}
		});
	}

	/**
	 * 模板方法, 处理异常的包装
	 * 
	 * @param callback
	 * @return
	 * @throws ZookeeperExpcetion
	 */
	public <V> V doTemplate(ZkCallback<V> callback)throws ZookeeperExpcetion{
		try {
			return callback.callback();
		} catch (Exception e) {
			throw new ZookeeperExpcetion(e);
		}
	}
	
	public boolean isStarted(){
		return getState() == CuratorFrameworkState.STARTED;
	}
	
	public CuratorFrameworkState getState(){
		if(zkClient != null ){
			return zkClient.getState();
		}
		return null;
	}
	
	private interface ZkCallback<V>{
		 V callback()throws Exception;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(zkClient, "Property 'zkClient' is required");
	}

	public void setZkClient(CuratorFramework zkClient) {
		this.zkClient = zkClient;
	}
}
