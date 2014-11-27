package com.easyooo.framework.cache.transaction;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easyooo.framework.cache.CacheException;
import com.easyooo.framework.cache.storage.JVMCache;

/**
 * JVM 本地缓存事务操作实现，该类只能直接使用
 * 
 * @see BufferedCacheRemoteSender
 * 
 * @author Killer
 */
public class DefaultJvmCacheTransactionCallback {
	
	private final Logger logger = LoggerFactory.getLogger(getClass()); 
	
	private Collection<Command> commands;
	
	private Map<String, Object> cacheMap;
	
	public DefaultJvmCacheTransactionCallback(Map<String, Object> cacheMap, Collection<Command> commands){
		this.commands = commands;
		this.cacheMap = cacheMap;
	}
	
	public void doCallback()throws CacheException {
		for (Command cmd : commands) {
			doProcessCommand(cmd);
		}
	}
	
	private void doProcessCommand(Command cmd){
		if(cmd.getOp() == null){
			logger.warn("No operations");
			return;
		}
		if(logger.isDebugEnabled()){
			logger.debug("Execute the command: " + cmd);
		}
		switch (cmd.getOp()) {
			case SET:
				set(cmd);
				break;
			case MOD:
				set(cmd);
				break;
			case DEL:
				del(cmd);
				break;
			case SETS: 
				sets(cmd);
				break;
			case ADD_MEMBERS:
				addMembers(cmd);
				break;
			case DEL_MEMBERS:
				delMembers(cmd);
				break;
			default:
				break;
		}
	}
	
	private void set(Command cmd){
		cacheMap.put(cmd.getCacheKey(), cmd.getCacheValue());
	}
	
	private void del(Command cmd){
		cacheMap.remove(cmd.getCacheKey());
	}
	
	private void sets(Command cmd){
		String[] keyvalues = cmd.getKeyvalues();
		for (int i = 0; i < keyvalues.length; i+=2) {
			String cacheKey = keyvalues[i];
			String value = keyvalues[i+1];
			cacheMap.put(cacheKey, value);
		}
	}

	private void addMembers(Command cmd){
		String groupKey = cmd.getCacheGroupKey();
		String[] members = cmd.getGroupValues();
		Object o = cacheMap.get(groupKey);
		
		if(o == null){
			if(logger.isDebugEnabled()){
				logger.debug("The local cache didn't find the group key["+ groupKey +"], Have been discarded");
			}
			return;
		}
		try {
			Set<String> memberSet = JVMCache.castToSet(groupKey, o);
			for (String member : members) {
				memberSet.add(member);
			}
		} catch (CacheException e) {
			logger.error("cast to set", e);
		}
	}
	
	private void delMembers(Command cmd){
		String groupKey = cmd.getCacheGroupKey();
		String[] members = cmd.getGroupValues();
		Object o = cacheMap.get(groupKey);
		
		if(o == null){
			if(logger.isDebugEnabled()){
				logger.debug("The local cache didn't find the group key["+ groupKey +"], Have been discarded");
			}
			return;
		}
		try {
			Set<String> memberSet = JVMCache.castToSet(groupKey, o);
			for (String member : members) {
				memberSet.remove(member);
			}
		} catch (CacheException e) {
			logger.error("cast to set", e);
		}
	}
}
