package com.easyooo.framework.sharding.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * 所有从数据源获取的连接对象，都不能直接关闭，由事务管理器统一关闭
 *
 * @author Killer
 */
public class CloseDelegatingConnection extends DelegatingConnection{

	public CloseDelegatingConnection(Connection delegate) {
		super(delegate);
	}
	
	/**
	 * 如果启用了多数据源事务管理，则不关闭资源，由事务管理器统一关闭
	 * @see #forceClose()
	 */
	@Override
	public void close() throws SQLException {
		if(!RoutingSynchronizationManager.isSynchronizationActive()){
			super.close();
		}
	}
	
	/**
	 * 该方法只会被事务资源回收才会被调用
	 * @throws SQLException
	 */
	protected void forceClose() throws SQLException {
		super.close();
	}

}
