/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.rule.impl;

import com.easyooo.framework.rule.Language;
import com.easyooo.framework.rule.Rule;

/**
 * 简单的规则实现
 * 
 * @author Killer
 */
public class SimpleRule implements Rule{

	private String ruleId;
	private String ruleText;
	private Language language;
	private Integer version;
	
	public SimpleRule(){
	}
	
	public SimpleRule(String ruleId, String ruleText, Language language, Integer version) {
		super();
		this.ruleId = ruleId;
		this.ruleText = ruleText;
		this.language = language;
		this.version = version;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public void setRuleText(String ruleText) {
		this.ruleText = ruleText;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Override
	public String getRuleId() {
		return this.ruleId;
	}

	@Override
	public String getRuleText() {
		return this.ruleText;
	}

	@Override
	public Language getLanguage() {
		return this.language;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public Integer getVersion() {
		return version;
	}
}
