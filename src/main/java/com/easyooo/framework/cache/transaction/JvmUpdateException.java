package com.easyooo.framework.cache.transaction;

/**
 * 
 * JVM缓存同步异常
 *
 * @author Killer
 */
@SuppressWarnings("serial")
public class JvmUpdateException extends Exception {
	
	
	public JvmUpdateException(String msg){
		super(msg);
	}
	
	public JvmUpdateException(String msg, Throwable e){
		super(msg, e);
	}

	public JvmUpdateException(Throwable e){
		super(e);
	}
}
