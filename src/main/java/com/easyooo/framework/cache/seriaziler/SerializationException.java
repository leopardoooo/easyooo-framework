package com.easyooo.framework.cache.seriaziler;

/**
 *
 * @author Killer
 */
@SuppressWarnings("serial")
public class SerializationException extends Exception {

	public SerializationException(String msg){
		super(msg);
	}
	
	public SerializationException(String msg, Throwable e){
		super(msg, e);
	}
	
}
