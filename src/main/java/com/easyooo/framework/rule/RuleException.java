package com.easyooo.framework.rule;

/**
 *
 * @author Killer
 */
public class RuleException extends Exception{

	private static final long serialVersionUID = 1L;

	public RuleException() {
		super();
	}

	public RuleException(String message) {
		super(message);
	}

	public RuleException(String message, Throwable e) {
		super(message, e);
	}

	public RuleException(Throwable e) {
		super(e);
	}
	
}
