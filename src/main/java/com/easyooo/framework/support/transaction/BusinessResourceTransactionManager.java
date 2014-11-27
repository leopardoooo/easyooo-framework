package com.easyooo.framework.support.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

import com.easyooo.framework.sharding.transaction.ChainedTransactionManager;

/**
 * <p>
 *  系统事务管理器，这里只是对事务资源的一种扩充，而多数据源的事务依赖父类来完成
 * </p>
 * <p>
 * 该类在数据库事务之后，将额外保证其它任何资源的事务一致性(这仅仅是一种理论上的事务一致性，一阶段事务)
 * 这部分资源包括只要实现了TransactionResource 接口的都将支持一阶段事务
 * </p>
 * 
 * @see TransactionResource
 * @see ChainedTransactionManager
 *
 * @author Killer
 */
public class BusinessResourceTransactionManager extends ChainedTransactionManager{
	
	Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public TransactionStatus getTransaction(TransactionDefinition definition)
			throws TransactionException {
		TransactionStatus status = super.getTransaction(definition);
		try {
			TransactionResourceManager.initSynchronization();
			triggerBegin(status);
		} catch (Throwable e) {
			throw new CannotCreateTransactionException(
					"Unable to open the transaction", e);
		}
		return status;
	}
	
	@Override
	public void commit(TransactionStatus status) throws TransactionException {
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
	public void rollback(TransactionStatus status) throws TransactionException {
		try{
			TransactionException rollbackException = null;
			try{
				super.rollback(status);
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
	protected void triggerBegin(TransactionStatus status)throws Throwable{
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
				logger.error("Resource commit error.", e);
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
				logger.error("Resource rollback error.", e);
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
