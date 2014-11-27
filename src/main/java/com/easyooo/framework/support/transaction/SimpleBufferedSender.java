package com.easyooo.framework.support.transaction;

import java.util.Collection;

/**
 * 发送缓冲器实现，委托依赖实际的发送器
 *
 * @author Killer
 */
public class SimpleBufferedSender<T> implements RemoteSenderOrderly<T>{
	
	private RemoteSender<T> delegate;
	private Object transactionSynchObjKey = new Object();
	
	public SimpleBufferedSender(RemoteSender<T> delegate){
		this.delegate = delegate;
	}
	
	@SuppressWarnings("unchecked")
	public boolean send(T msg) throws Exception {
		if (!TransactionResourceManager.hasResource(transactionSynchObjKey)) {
			TransactionResourceManager.bindResource(transactionSynchObjKey,
					new SimpleBufferedTransactionResource<T>(delegate));
		}
		SimpleBufferedTransactionResource<T> buffer = (SimpleBufferedTransactionResource<T>) TransactionResourceManager
				.getResource(transactionSynchObjKey);
		buffer.send(msg);
		return true;
	}
	
	public Collection<T> getOriginalBufferList(){
		if (TransactionResourceManager.hasResource(transactionSynchObjKey)) {
			SimpleBufferedTransactionResource<T> buffer = TransactionResourceManager
					.getResource(transactionSynchObjKey);
			return buffer.getOriginalBufferList();
		}
		return null;
	}

	@Override
	public String getProviderInfo() {
		return delegate.getProviderInfo();
	}
}
