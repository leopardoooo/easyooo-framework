package com.easyooo.framework.configure;


/**
 *
 * @author Killer
 */
@SuppressWarnings("serial")
public class UnifiedConfigException extends Throwable {

	public UnifiedConfigException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public UnifiedConfigException(Throwable cause) {
		super(cause);
	}
	
	public UnifiedConfigException(String msg) {
		super(msg);
	}
}
