package com.easyooo.framework.support.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 状态变更的监听器
 *
 * @author Killer
 */
public class LogConnectionStateListener implements ConnectionStateListener,
		UnhandledErrorListener {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void stateChanged(CuratorFramework client, ConnectionState newState) {
		logger.info("Zookeeper client 状态发生变化: " + newState);
	}

	@Override
	public void unhandledError(String message, Throwable e) {
		logger.info("Zookeeper unhandledError: " + message);
		e.printStackTrace();
	}

}
