package com.easyooo.framework.cache.transaction;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easyooo.framework.cache.storage.JVMCache;
import com.easyooo.framework.support.redis.RedisTemplate;
import com.easyooo.framework.support.transaction.RemoteSenderMorly;

/**
 * 缓存批量发送器，将缓存一次性写入目标服务器
 *
 * @author Killer
 */
public class BufferedCacheRemoteSender implements RemoteSenderMorly<Command>{

	private final Logger logger = LoggerFactory.getLogger(getClass()); 
	
	private static final String PROVIDER = "EASYOOO_CACHE"; 
	
	/** 依赖外部注入 */
	private RedisTemplate redisTemplate;
	/** 依赖外部注入 */
	private UpdateCommandProducer updateCommandProducer;
	
	/** 本地缓存引用，for lock */
	private final JVMCache jvmCacheObj = new JVMCache();
	
	public BufferedCacheRemoteSender(RedisTemplate redisTemplate, UpdateCommandProducer updateCommandProducer){
		this.redisTemplate = redisTemplate;
		this.updateCommandProducer = updateCommandProducer;
	}
	
	@Override
	public String getProviderInfo() {
		return PROVIDER;
	}

	@Override
	public boolean send(Collection<Command> commands) throws Exception {
		// 提取出JVM本地命令及redis缓存命令
		Collection<Command> jvmCommands = new ArrayList<>();
		Collection<Command> redisCommands = new ArrayList<>();
		for (Command cmd : commands) {
			switch (cmd.getLevel()) {
				case JVM:
					jvmCommands.add(cmd);
					break;
				case REDIS:
					redisCommands.add(cmd);
					break;
				case JVM_TO_REDIS:
					jvmCommands.add(cmd);
					redisCommands.add(cmd);
					break;
				default:
					break;
			}
		}
		
		// send redis cache
		DefaultRedisTransactionCallback drtc = new DefaultRedisTransactionCallback(
				redisCommands);
		
		// send jvm cache
		DefaultJvmCacheTransactionCallback djt = new DefaultJvmCacheTransactionCallback(jvmCacheObj.getCacheObject(), jvmCommands);
		
		// lock cache object
		
		int jSize = jvmCommands.size(), rRize = redisCommands.size();
		
		if(jSize > 0 && rRize > 0){
			synchronized (jvmCacheObj.getCacheObject()) {
				synchronized (redisTemplate) {
					try{
						redisTemplate.transaction(drtc);
					}catch(UnsupportedOperationException e){
						redisTemplate.pipelined(drtc);
					}
					djt.doCallback();
				}
			}
		}else if(jSize == 0 && rRize > 0) {
			synchronized (redisTemplate) {
				try{
					redisTemplate.transaction(drtc);
				}catch(UnsupportedOperationException e){
					redisTemplate.pipelined(drtc);
				}
			}
		}else if(jSize > 0 && rRize == 0){
			synchronized (jvmCacheObj.getCacheObject()) {
				djt.doCallback();
			}
		}else{
			logger.info("No command to submit.");
		}
		// update to the other jvm process 
		updateCommandProducer.submitUpdate(jvmCommands);
		return true;
	}
}