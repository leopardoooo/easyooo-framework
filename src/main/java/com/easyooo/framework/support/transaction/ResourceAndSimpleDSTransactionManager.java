package com.easyooo.framework.support.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * 一个简单的事务管理器，支持单数据源、MQ、缓存、Socket等资源的一阶段事务提交,
 * 支持扩展Buffer Queue模式
 * 
 * @see TransactionResourceManager
 * @see SimpleBufferedTransactionResource
 * 
 * @author Killer
 */
@SuppressWarnings("serial")
public class ResourceAndSimpleDSTransactionManager extends DataSourceTransactionManager {
	Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * This implementation sets the isolation level but ignores the timeout.
	 */
	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) {
		super.doBegin(transaction, definition);
		try {
			TransactionResourceManager.initSynchronization();
			triggerBegin();
		} catch (Throwable e) {
			throw new CannotCreateTransactionException(
					"Unable to begin the transaction", e);
		}
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status) {
		try{
			super.commit(status);
			triggerCommit(status);
		}catch(TransactionException te){
			triggerRollback(status);
			throw te;
		}finally{
			// finally将保证cleanup被调用
			cleanup();
		}
	}

	
	@Override
	protected void doRollback(DefaultTransactionStatus status) {
		try{
			TransactionException rollbackException = null;
			try{
				super.doRollback(status);
			}catch(TransactionException te){
				rollbackException = te;
			}
			
			// 保证触发资源回滚
			triggerRollback(status);
			
			if(rollbackException != null){
				throw rollbackException; 
			}
		}finally{
			cleanup();
		}
	}
	
	/**
	 * 触发事务开始方法 
	 */
	protected void triggerBegin()throws Throwable{
		Map<Object, TransactionResource> trs = TransactionResourceManager.getResourceMap();
		for (TransactionResource tr : trs.values()) {
			tr.begin();
		}
	}
	
	/**
	 * 触发资源提交，使得每一个资源实例都触发提交，
	 * 如果一旦出现异常，简单的记录日志，并继续下一资源的提交任务
	 * 
	 * @param status
	 */
	protected void triggerCommit(TransactionStatus status){
		Map<Object, TransactionResource> trs = TransactionResourceManager.getResourceMap();
		List<Throwable> commitException = new ArrayList<>();
		List<TransactionResource> res = new ArrayList<>();
		for (TransactionResource tr : trs.values()) {
			try {
				tr.commit();
			} catch (Throwable e) {
				commitException.add(e);
				res.add(tr);
			}
		}
		
		if(commitException.size() > 0){
			logger.info("The transaction resource commit failure. total " + res.size());
		}
	}
	
	/**
	 * 触发资源回滚，使得每一个资源实例都触发回滚
	 * @param status
	 */
	protected void triggerRollback(TransactionStatus status){
		Map<Object, TransactionResource> trs = TransactionResourceManager.getResourceMap();
		List<Throwable> rollbackException = new ArrayList<>();
		List<TransactionResource> res = new ArrayList<>();
		for (TransactionResource tr : trs.values()) {
			try {
				tr.rollback();
			} catch (Throwable e) {
				rollbackException.add(e);
				res.add(tr);
			}
		}
		
		if(rollbackException.size() > 0){
			logger.info("The transaction resource rollback failure. total " + res.size());
		}
	}
	
	/**
	 * 事务资源的清理任务
	 * 清理线程变量
	 */
	protected void cleanup(){
		TransactionResourceManager.clear();
		
		if(logger.isDebugEnabled()){
			logger.debug("Transaction Resources has clean up.");
		}
	}
	

}
