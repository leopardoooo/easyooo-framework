package com.easyooo.framework.cache.transaction;

import java.util.Collection;

import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.Transaction;

import com.easyooo.framework.support.redis.PiplineCallbackAdapter;
import com.easyooo.framework.support.redis.TransactionCallback;

/**
 * 执行Redis命令，支持事务及管道模式两种方式
 * 
 * @author Killer
 */
public class DefaultRedisTransactionCallback extends PiplineCallbackAdapter implements TransactionCallback{
	
	private Collection<Command> redisCommands;
	
	public DefaultRedisTransactionCallback(Collection<Command> redisCommands){
		this.redisCommands = redisCommands;
	}

	@Override
	public void doCallback(Transaction tx) {
		for (Command cmd : redisCommands) {
			switch (cmd.getOp()) {
				case SET:
					tx.set(cmd.getCacheKey(), cmd.getCacheValue());
					break;
				case MOD:
					tx.set(cmd.getCacheKey(), cmd.getCacheValue());
					break;
				case DEL:
					tx.del(cmd.getCacheKey());
					break;
				case ADD_MEMBERS:
					tx.sadd(cmd.getCacheGroupKey(), cmd.getGroupValues());
					break;
				case DEL_MEMBERS:
					tx.srem(cmd.getCacheGroupKey(), cmd.getGroupValues());
					break;
				case SETS:
					tx.mset(cmd.getKeyvalues());
				default:
					break;
			}
		}
	}
	
	@Override
	public void doCallback(ShardedJedisPipeline p) {
		for (Command cmd : redisCommands) {
			switch (cmd.getOp()) {
				case SET:
					p.set(cmd.getCacheKey(), cmd.getCacheValue());
					break;
				case MOD:
					p.set(cmd.getCacheKey(), cmd.getCacheValue());
					break;
				case DEL:
					p.del(cmd.getCacheKey());
					break;
				case ADD_MEMBERS:
					p.sadd(cmd.getCacheGroupKey(), cmd.getGroupValues());
					break;
				case DEL_MEMBERS:
					p.srem(cmd.getCacheGroupKey(), cmd.getGroupValues());
					break;
				case SETS:
					String[] keyvalues = cmd.getKeyvalues();
					for (int i = 0; i < keyvalues.length; i+=2) {
						p.set(keyvalues[i], keyvalues[i+1]);
					}
				default:
					break;
			}
		}
	}
	
}
