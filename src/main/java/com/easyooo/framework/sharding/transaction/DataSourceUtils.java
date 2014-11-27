package com.easyooo.framework.sharding.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 
 * JDBC连接帮助类，包括事务管理器
 * 
 * @author Killer
 */
public abstract class DataSourceUtils {

	private static final Logger logger = LoggerFactory.getLogger(DataSourceUtils.class);

	/**
	 * 从数据源获取一个连接，该连接是否新连接，取决于连接池厂商是否会缓存Connection的策略
	 * 
	 * @param dataSource 
	 * @return
	 * @throws CannotGetJdbcConnectionException
	 */
	public static Connection getConnection(DataSource dataSource)
			throws CannotGetJdbcConnectionException {
		try {
			return doGetConnection(dataSource, null, null);
		} catch (SQLException ex) {
			throw new CannotGetJdbcConnectionException(
					"Could not get JDBC Connection", ex);
		}
	}
	
	public static Connection getConnection(DataSource dataSource, String user, String pwd)
			throws CannotGetJdbcConnectionException {
		try {
			return doGetConnection(dataSource, user, pwd);
		} catch (SQLException ex) {
			throw new CannotGetJdbcConnectionException(
					"Could not get JDBC Connection", ex);
		}
	}

	/**
	 * 获取一个连接池，将连接池绑定到当前线程变量，在调用该方法的同时，
	 * 需要显示调用 {@link #releaseConnection(Connection, DataSource)}
	 * 
	 * @param dataSource
	 * @return
	 * @throws SQLException
	 */
	public static Connection doGetConnection(DataSource dataSource, String user, String password)
			throws SQLException {
		Assert.notNull(dataSource, "No DataSource specified");
		ConnectionHolder holder = getCurrentConnectionHolder(dataSource, user, password);
		if(holder == null){
			Connection conn = null;
			if(StringUtils.isEmpty(user)){
				conn = dataSource.getConnection();	
			}else{
				conn = dataSource.getConnection(user, password);
			}
			holder = new ConnectionHolder(new CloseDelegatingConnection(conn), user, password);
			if (RoutingSynchronizationManager.isSynchronizationActive()) {
				RoutingSynchronizationManager.registerSynchronization(dataSource, holder);
				doBegin(conn, dataSource);
				prepareConnectionForTransaction(conn);
			}
		}
		holder.requested();
		if (logger.isDebugEnabled()) {
			logger.debug("Connection["+ holder.getConn() +"] is used "
					+ holder.getReferenceCount() +" times.");
		}
		return holder.getConn();
	}
	
	
	/**
	 * Get opened connection in the given datasource
	 * @param dataSource
	 * @param user
	 * @param password
	 */
	public static ConnectionHolder getCurrentConnectionHolder(DataSource dataSource, String user, String password){
		if(!RoutingSynchronizationManager.isSynchronizationActive()){
			return null;
		}
		ConnectionHolder holder = RoutingSynchronizationManager.getSynchronizations().get(dataSource);
		if(holder != null && holder.equalUserAndPwd(user, password)){
			return holder;
		}
		return null;
	}

	public static void doBegin(Connection con, DataSource dataSource) {
		try {
			if (con.getAutoCommit()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Switching JDBC Connection [" + con + "] to manual commit");
				}
				con.setAutoCommit(false);
			}
		}
		catch (Throwable ex) {
			DataSourceUtils.releaseConnection(con);
			throw new CannotCreateTransactionException("Could not open JDBC Connection for transaction", ex);
		}
	}
	
	public static Integer prepareConnectionForTransaction(Connection con) throws SQLException {
		Assert.notNull(con, "No Connection specified");
		TransactionDefinition definition = RoutingSynchronizationManager.getCurrentTransactionDefinition();
		if (definition != null && definition.isReadOnly()) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Setting JDBC Connection [" + con + "] read-only");
				}
				con.setReadOnly(true);
			} catch (SQLException ex) {
				Throwable exToCheck = ex;
				while (exToCheck != null) {
					if (exToCheck.getClass().getSimpleName().contains("Timeout")) {
						throw ex;
					}
					exToCheck = exToCheck.getCause();
				}
				logger.debug("Could not set JDBC Connection read-only", ex);
			} catch (RuntimeException ex) {
				Throwable exToCheck = ex;
				while (exToCheck != null) {
					if (exToCheck.getClass().getSimpleName().contains("Timeout")) {
						throw ex;
					}
					exToCheck = exToCheck.getCause();
				}
				logger.debug("Could not set JDBC Connection read-only", ex);
			}
		}

		// Apply specific isolation level, if any.
		Integer previousIsolationLevel = null;
		if (definition != null
				&& definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
			if (logger.isDebugEnabled()) {
				logger.debug("Changing isolation level of JDBC Connection ["
						+ con + "] to " + definition.getIsolationLevel());
			}
			int currentIsolation = con.getTransactionIsolation();
			if (currentIsolation != definition.getIsolationLevel()) {
				previousIsolationLevel = currentIsolation;
				con.setTransactionIsolation(definition.getIsolationLevel());
			}
		}

		return previousIsolationLevel;
	}

	public static void releaseConnection(Connection con) {
		try {
			doCloseConnection(con);
		} catch (SQLException ex) {
			logger.debug("Could not close JDBC Connection", ex);
		} catch (Throwable ex) {
			logger.debug("Unexpected exception on closing JDBC Connection", ex);
		}
	}

	public static void doCloseConnection(Connection con)
			throws SQLException {
		con.close();
	}

}
