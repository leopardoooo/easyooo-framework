package com.easyooo.framework.sharding.transaction;

import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.HeuristicCompletionException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationUtils;

/**
 * 一阶段模式事务管理链，代理获取到所有数据源管理器，链式提交事务，在最后提交事务
 *
 * @author Killer
 */
public class ChainedTransactionManager implements PlatformTransactionManager{
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public TransactionStatus getTransaction(TransactionDefinition definition)
			throws TransactionException {
		if(logger.isDebugEnabled()){
			logger.debug("Open a one-pc transaction.");
		}
		
		RoutingSynchronizationManager.initSynchronization(definition);
		return newTransactionStatus(definition, new TransactionObject());
	}
	
	/**
	 * Create a rae TransactionStatus instance for the given arguments.
	 */
	protected DefaultTransactionStatus newTransactionStatus(
			TransactionDefinition definition, Object transaction) {
		// Cache debug flag to avoid repeated checks.
		boolean debugEnabled = logger.isDebugEnabled();
		
		return new DefaultTransactionStatus(
				transaction, true, true,
				definition.isReadOnly(), debugEnabled, null);
	}

	@Override
	public void commit(TransactionStatus status) throws TransactionException {
		if(logger.isDebugEnabled()){
			logger.debug("Commit all connected transactions.");
		}
		DefaultTransactionStatus defStatus = (DefaultTransactionStatus)status;
		try{
			TransactionSynchronizationUtils.triggerBeforeCommit(defStatus.isReadOnly());
			TransactionSynchronizationUtils.triggerBeforeCompletion();
			Map<DataSource, ConnectionHolder> connSet = RoutingSynchronizationManager.getSynchronizations();
			boolean commit = true;
	        Exception commitException = null;
	        ConnectionHolder commitExceptionConnection = null;
	
	        for (ConnectionHolder connection : connSet.values()) {
	            if (commit) {
	                try {
	                	connection.commit();
	                	if (logger.isDebugEnabled()) {
		        			logger.debug("Connection["+ connection +"] has been commited.");
		        		}
	                } catch (Exception ex) {
	                    commit = false;
	                    commitException = ex;
	                    commitExceptionConnection = connection;
	                }
	            } else {
	                try {
	                	connection.rollback();
	                	if (logger.isDebugEnabled()) {
		        			logger.debug("Connection["+ connection +"] has been rolled back.");
		        		}
	                } catch (Exception ex) {
	                    logger.warn("Rollback exception (after commit) (" + connection + ") " + ex.getMessage(), ex);
	                }
	            }
	        }
	
			if (commitException != null) {
				boolean firstTransactionManagerFailed = (commitExceptionConnection == getLastConnectionHolder(connSet));
				int transactionState = firstTransactionManagerFailed ? HeuristicCompletionException.STATE_ROLLED_BACK
						: HeuristicCompletionException.STATE_MIXED;
				throw new HeuristicCompletionException(transactionState,
						commitException);
			}
		}finally{
			RoutingSynchronizationManager
					.invokeAfterCompletion(TransactionSynchronization.STATUS_COMMITTED);
			RoutingSynchronizationManager.clearSynchronization();
		}
	}

	@Override
	public void rollback(TransactionStatus status) throws TransactionException {
		if(logger.isWarnEnabled()){
			logger.warn("Rollback all connected transactions.");
		}
		try{
			TransactionSynchronizationUtils.triggerBeforeCompletion();
			Map<DataSource, ConnectionHolder> connSet = RoutingSynchronizationManager.getSynchronizations();
			Exception rollbackException = null;
			ConnectionHolder rollbackExceptionConnection = null;
	
	        for (ConnectionHolder connection:connSet.values()) {
	            try {
	            	connection.rollback();
	            	if (logger.isDebugEnabled()) {
	        			logger.debug("Connection["+ connection +"] has been rolled back.");
	        		}
	            } catch (Exception ex) {
	                if (rollbackException == null) {
	                    rollbackException = ex;
	                    rollbackExceptionConnection = connection;
	                } else {
	                    logger.warn("Rollback exception (" + rollbackExceptionConnection + ") " + ex.getMessage(), ex);
	                }
	            }
	        }
	        
	        if (rollbackException != null) {
	            throw new UnexpectedRollbackException("Rollback exception, originated at ("+rollbackExceptionConnection+") "+
	              rollbackException.getMessage(), rollbackException);
	        }
		}finally{
			RoutingSynchronizationManager
					.invokeAfterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);
			RoutingSynchronizationManager.clearSynchronization();
		}
	}
	
	private ConnectionHolder getLastConnectionHolder(Map<DataSource, ConnectionHolder> connSet) {
		if(connSet.size() != 0){
			int lastConnectionIndex = connSet.size() - 1;
			Iterator<ConnectionHolder> ite = connSet.values().iterator();
			for (int i = 0; ite.hasNext(); i++) {
				ConnectionHolder conn = ite.next();
				if(i == lastConnectionIndex){
					return conn;
				}
			}
		}
		return null;
    }
	
	public static class TransactionObject{
		// Empty Object
	}
	
}
