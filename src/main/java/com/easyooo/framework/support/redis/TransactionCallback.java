package com.easyooo.framework.support.redis;

import redis.clients.jedis.Transaction;


/**
 * 
 * callback for redis transaction
 * 
 * @author Killer
 */
public interface TransactionCallback{
	
	void doCallback(Transaction trans);

}
