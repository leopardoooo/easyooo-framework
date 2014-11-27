package com.easyooo.framework.common.util;

import lombok.ToString;

/**
 *
 * @author Killer
 */
@ToString
public class TestObject2 {
	
	private Integer id;
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
}
