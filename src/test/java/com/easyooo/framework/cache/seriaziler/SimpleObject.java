package com.easyooo.framework.cache.seriaziler;

import lombok.ToString;

/**
 *
 * @author Killer
 */
@ToString
public class SimpleObject {
	
	private String id;
	
	private String name;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTestId(){
		return this.id;
	}
	
}