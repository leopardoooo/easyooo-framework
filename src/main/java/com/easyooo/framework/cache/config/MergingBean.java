/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Killer
 */
public class MergingBean {

	private Class<?> targetClass;

	private Map<Class<?>, List<RefrenceBean>> refrences = 
			new HashMap<Class<?>, List<RefrenceBean>>();

	public MergingBean() {
		super();
	}

	public MergingBean(Class<? extends Object> targetClass,
			Map<Class<?>, List<RefrenceBean>> refrences) {
		super();
		
		this.targetClass = targetClass;
		this.refrences = refrences;
	}

	public Class<? extends Object> getTargetClass() {
		return targetClass;
	}

	public Map<Class<?>, List<RefrenceBean>> getRefrences() {
		return refrences;
	}

	public void setRefrences(Map<Class<?>, List<RefrenceBean>> refrences) {
		this.refrences = refrences;
	}

	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}
	
}
