package com.easyooo.framework.support.transaction;


/**
 * 
 * 事务资源接口定义，在事务流程中，事务各环节都会触发资源的提交或回滚，
 * 
 * 该接口属于用户自定义事务资源，可以通过该类实现具体的事务资源控制，
 * 在事务commit/rollback时会调用接口方法，实现一阶段事务统一控制
 *
 * 该接口的方法总是在数据库之后才会被触发
 * 
 * @author Killer
 */
public interface TransactionResource {

	/**
	 * 事务启动的接口方法
	 * @throws Throwable
	 */
	void begin()throws Throwable;
	
	/**
	 * 事务提交操作会调用
	 * @throws Throwable
	 */
	void commit() throws Throwable;
	
	
	/**
	 * 回滚操作
	 * @throws Throwable
	 */
	void rollback() throws Throwable;
	
}
