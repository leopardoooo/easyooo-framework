package com.easyooo.framework.sharding.transaction;

import javax.sql.DataSource;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

/**
 * 
 * Creates a {@code RountingManagedTransactionFactory}.
 * 
 * @author Killer
 */
public class RountingManagedTransactionFactory extends SpringManagedTransactionFactory {

	@Override
	public Transaction newTransaction(DataSource dataSource,
			TransactionIsolationLevel level, boolean autoCommit) {
		return new RoutingSpringManagedTransaction(dataSource);
	}

}
