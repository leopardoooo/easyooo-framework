/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache;

/**
 *
 * @author Killer
 */
public class DataProxyException extends Throwable {

	private static final long serialVersionUID = 1L;

	public DataProxyException() {
		super();
	}

	public DataProxyException(String message) {
		super(message);
	}

	public DataProxyException(String message, Throwable e) {
		super(message, e);
	}

	public DataProxyException(Throwable e) {
		super(e);
	}
	
	
	public static DataProxyException newIt(Throwable e){
		return new DataProxyException(e);
	}

}