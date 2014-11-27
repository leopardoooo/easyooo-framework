package com.easyooo.framework.sharding.transaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronizationUtils;
import org.springframework.util.Assert;

/**
 * 多数据同步管理器，将一些上下文绑定到ThreadLocal
 *
 * @author Killer
 */
public abstract class RoutingSynchronizationManager {
	
	static Logger logger = LoggerFactory.getLogger(RoutingSynchronizationManager.class);
	
	private static final ThreadLocal<LinkedHashMap<DataSource, ConnectionHolder>> synchronizations =
			new NamedThreadLocal<LinkedHashMap<DataSource, ConnectionHolder>>("Transactional synchronizations");
	
	private static final ThreadLocal<TransactionDefinition> definitions =
			new NamedThreadLocal<TransactionDefinition>("Transactional definition");
	
	public static TransactionDefinition getCurrentTransactionDefinition(){
		if (!isSynchronizationActive()) {
			throw new IllegalStateException("Transaction synchronization is not active");
		} 
		return definitions.get();
	}
	
	public static boolean isSynchronizationActive() {
		return (synchronizations.get() != null);
	}
	
	public static void initSynchronization(TransactionDefinition definition) throws IllegalStateException {
		if (isSynchronizationActive()) {
			throw new IllegalStateException("Cannot activate transaction synchronization - already active");
		}
		if(logger.isDebugEnabled()){
			logger.debug("Initializing transaction synchronization");
		}
		synchronizations.set(new LinkedHashMap<DataSource, ConnectionHolder>());
		definitions.set(definition);
		
		// 初始化Spring同步事务管理器，这使得Mybatis让Spring接管事务时，有一个判断标准，
		// 不会重复打开Session，也不会直接关闭Session
		// 具体请参看 org.mybatis.spring.SqlSessionUtils#getSqlSession
		// org.mybatis.spring.SqlSessionTemplate.SqlSessionInterceptor#invoke
		TransactionSynchronizationManager.initSynchronization();
	}

	public static void registerSynchronization(DataSource dataSource, ConnectionHolder connection)
			throws IllegalStateException {
		Assert.notNull(connection, "connection must not be null");
		if (!isSynchronizationActive()) {
			throw new IllegalStateException("Transaction synchronization is not active");
		}
		synchronizations.get().put(dataSource, connection);
	}
	
	public static Map<DataSource, ConnectionHolder> getSynchronizations() throws IllegalStateException {
		Map<DataSource,ConnectionHolder> synchs = synchronizations.get();
		if (synchs == null) {
			throw new IllegalStateException("Transaction synchronization is not active");
		}
		if (synchs.isEmpty()) {
			return Collections.emptyMap();
		}
		return synchs;
	}
	
	public static void clearSynchronization() throws IllegalStateException {
		if (!isSynchronizationActive()) {
			throw new IllegalStateException("Cannot deactivate transaction synchronization - not active");
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("Transaction cleaning.");
		}
		
		try{
			Map<DataSource,ConnectionHolder> synchs = synchronizations.get();
			List<SQLException> exceptions = new ArrayList<SQLException>();
			for (ConnectionHolder connection : synchs.values()) {
				try {
					connection.close();
				} catch (SQLException e) {
					exceptions.add(e);
				}
			}
			
			if(!exceptions.isEmpty()){
				for (SQLException sqlException : exceptions) {
					logger.error("Close the connection error", sqlException);
				}
			}
		}finally{
			synchronizations.remove();
			definitions.remove();
			// Clean up the Spring transaction synch
			TransactionSynchronizationManager.clear();	
		}
	}
	
	public static void invokeAfterCompletion(int completionStatus) {
		List<TransactionSynchronization> synchronizations = TransactionSynchronizationManager
				.getSynchronizations();
		TransactionSynchronizationUtils.invokeAfterCompletion(synchronizations, completionStatus);
	}
	
}
