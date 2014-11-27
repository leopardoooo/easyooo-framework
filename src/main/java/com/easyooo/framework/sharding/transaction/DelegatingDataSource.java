package com.easyooo.framework.sharding.transaction;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * 
 * JDBC数据源委托实现，委托所有的方法给目标{@link javax.sql.DataSource}
 *
 * 这个类主要重写{@link #getConnection()}，不是直接委托到目标数据源。
 *
 * @author Killer
 */
public class DelegatingDataSource implements DataSource {
	
	private DataSource targetDataSource;
	
	/**
	 * 创建一个新的数据源.
	 * @param targetDataSource the target DataSource
	 */
	public DelegatingDataSource(DataSource targetDataSource) {
		this.targetDataSource = targetDataSource;
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		return getConnection0(null, null);
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return getConnection0(username, password);
	}
	
	private Connection getConnection0(String username, String password)throws SQLException{
		return DataSourceUtils.getConnection(this.targetDataSource, username, password);
	}
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return this.targetDataSource.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		this.targetDataSource.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		this.targetDataSource.setLoginTimeout(seconds);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return this.targetDataSource.getLoginTimeout();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return this.targetDataSource.getParentLogger();
	}

	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if (iface.isInstance(this)) {
			return (T) this;
		}
		return targetDataSource.unwrap(iface);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return (iface.isInstance(this) || targetDataSource.isWrapperFor(iface));
	}

}
