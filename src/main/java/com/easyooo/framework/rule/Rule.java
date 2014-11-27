package com.easyooo.framework.rule;

/**
 * 规则配置接口定义，外部传入的规则需要实现该类
 * 
 * @author Killer
 */
public interface Rule {
	
	/**
	 * 获取规则编号，需要保持唯一
	 * @return
	 */
	public String getRuleId();
	
	
	/**
	 * 规则内容文本
	 * 
	 * @return
	 */
	public String getRuleText();
	
	
	/**
	 * 规则语言实用的语言
	 * 
	 * @see Language
	 * @return
	 */
	public Language getLanguage();
	
	
	/**
	 * 获取版本号
	 * @return
	 */
	public Integer getVersion();
	
}
