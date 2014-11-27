package com.easyooo.framework.sharding;

/**
 *
 * @author Killer
 */
@SuppressWarnings("serial")
public class DsRoutingException extends RuntimeException {

	private Module module;
	private Long keyword;
	
	public DsRoutingException(String msg){
		super(msg);
	}
	
	public DsRoutingException(String msg, Throwable e){
		super(msg, e);
	}
	
	public DsRoutingException(Throwable e){
		super(e);
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	public Long getKeyword() {
		return keyword;
	}

	public void setKeyword(Long keyword) {
		this.keyword = keyword;
	}
	
}
