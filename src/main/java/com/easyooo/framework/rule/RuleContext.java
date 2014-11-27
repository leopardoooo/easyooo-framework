package com.easyooo.framework.rule;

import java.util.Map;


/**
 * 规则上下文定义
 * 
 * @author Killer
 */
public class RuleContext{
	
	private Map<String,Object> context;
	
	public RuleContext(Map<String, Object> context){
		this.context = context;
	}
	
	public Map<String, Object> getContext() {
		return context;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key){
		if(context.containsKey(key)){
			return (T)context.get(key);
		}
		return null;
	}

}
