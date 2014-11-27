package com.easyooo.framework.support.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缓冲事务资源，所有加入的只有在事务完成才会提交
 *
 * @author Killer
 */
public class SimpleBufferedTransactionResource<T> implements TransactionResource {

	Logger logger = LoggerFactory.getLogger(getClass());
	
	private final Collection<T> buffer = new ArrayList<T>();
	
	private AtomicInteger requested = new AtomicInteger();
	
	private RemoteSender<T> sender;
	
	private String provider = "UNKNOWN";
	
	public SimpleBufferedTransactionResource(RemoteSender<T> directSender){
		this.sender = directSender;
		this.provider = directSender.getProviderInfo();
	}
	
	@Override
	public void begin() throws Throwable {
		// do nothing
	}

	@Override
	public void commit() throws Throwable {
		if(buffer.size() > 0){
			if(sender instanceof RemoteSenderMorly){
				if (logger.isDebugEnabled()) {
					logger.debug("Morly["+ sender.getProviderInfo() +"] sender ready");
				}
				RemoteSenderMorly<T> rsm = ((RemoteSenderMorly<T>) sender);
				rsm.send(buffer);
			}else if(sender instanceof RemoteSenderOrderly){
				if (logger.isDebugEnabled()) {
					logger.debug("Orderly["+ sender.getProviderInfo() +"] sender ready");
				}
				
				RemoteSenderOrderly<T> rso = ((RemoteSenderOrderly<T>) sender);
				Iterator<T> iterator = buffer.iterator();
				for (;iterator.hasNext();) {
					T data = iterator.next();
					rso.send(data);
				}
			}
			
			
			if (logger.isInfoEnabled()) {
				logger.info(String
						.format("[%s] buffer has been flushed to the server, total %d items",
								provider, requested.get()));
			}
		}else{
			logger.warn("No need to send message buffers");
		}
	}

	@Override
	public void rollback() throws Throwable {
		if(logger.isDebugEnabled()){
			logger.info("Transaction Resource has been rolled back.");
		}
		buffer.clear();
	}
	
	public void send(T data){
		if(data == null){
			return ;
		}
		buffer.add(data);
		requested.incrementAndGet();
		
		
		if(logger.isDebugEnabled()){
			logger.debug(String.format("[%s] buffer has joined %d messages",
				provider, requested.get()));
		}
	}
	
	/**
	 * @return 返回一个副本
	 */
	public Collection<T> getBufferList(){
		return new CopyOnWriteArrayList<T>(buffer);
	}
	
	/**
	 * 通常情况下返回的集合只读，但也可以自行修改，但请慎重修改
	 * 通常应该调用@see {@link #getBufferList()}
	 * @return 返回原始的缓冲数组
	 */
	protected Collection<T> getOriginalBufferList(){
		return buffer;
	}
}
