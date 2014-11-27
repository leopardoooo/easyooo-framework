package com.easyooo.framework.sharding.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionUtils;
import org.mybatis.spring.transaction.SpringManagedTransaction;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * 
 * 接管MyBatis Spring 事务，如果使用了多数据源的事务则由Spring拦截器统一管理事务
 * <code>openTransaction\commit\rollback</code>
 * 
 * 如果在无事务的情况下，则会每次操作数据库都打开新的连接，自动commit/rollbak， 在返回结果之前关闭连
 * 接，效率较低。
 * 
 * 建议：使用Spring代理所有有关数据库的事务，包括只读事务，
 * 这样每次操作一个数据源只会打开一个连接 同库操作多次时只会使用第一次打开的连接。
 * 
 * @see com.easyooo.show.business.support.sharding.transaction.DataSourceUtils#getConnection(DataSource)
 * 
 * @see TransactionInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
 * @see RoutingSynchronizationManager
 * @see SqlSessionFactoryBean
 * @see SqlSessionUtils#getSqlSession(org.apache.ibatis.session.SqlSessionFactory,
 *      org.apache.ibatis.session.ExecutorType,
 *      org.springframework.dao.support.PersistenceExceptionTranslator)
 * 
 * @author Killer
 */
public class RoutingSpringManagedTransaction extends SpringManagedTransaction {
	private final DataSource dataSource;
	
	private Connection nonTransactionConnection;
	
	public RoutingSpringManagedTransaction(DataSource dataSource) {
		super(dataSource);
		this.dataSource = dataSource;
	}

	/**
	 * 多数据源的情况下不能缓存同一个连接，因此每次都从数据源获取一个连接，
	 * 只有在同一个数据源的情况下是可以持有一个连接对象
	 * 
	 * @see DataSourceUtils#getConnection(DataSource)
	 */
	public Connection getConnection() throws SQLException {
		if(hasRoutingDataSourceTransactionManager()){
			return openConnection();
		}else{
			return this.nonTransactionConnection = openConnection();
		}
	}

	private Connection openConnection() throws SQLException {
		return this.dataSource.getConnection();
	}

	public void commit() throws SQLException {
		if(hasRoutingDataSourceTransactionManager()){
			// Does nothing
		}else{
			super.commit();
		}
	}

	public void rollback() throws SQLException {
		if(hasRoutingDataSourceTransactionManager()){
			// Does nothing
		}else{
			super.rollback();
		}
	}

	public void close() throws SQLException {
		if(nonTransactionConnection == null){
			// Does nothing
		}else{
			DataSourceUtils.releaseConnection(this.nonTransactionConnection, null);
		}
	}
	
	protected boolean hasRoutingDataSourceTransactionManager(){
		return RoutingSynchronizationManager.isSynchronizationActive();
	}
}
