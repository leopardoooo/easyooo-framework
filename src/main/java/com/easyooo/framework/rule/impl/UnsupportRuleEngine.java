package com.easyooo.framework.rule.impl;

import com.easyooo.framework.rule.Rule;
import com.easyooo.framework.rule.RuleContext;
import com.easyooo.framework.rule.RuleEngine;
import com.easyooo.framework.rule.RuleException;

/**
 * @author Killer
 */
public class UnsupportRuleEngine implements RuleEngine {

	@Override
	public <T> T eval(Rule rule) throws RuleException {
		return throwe();
	}

	@Override
	public void setContext(RuleContext context) {
		// do nothing
	}

	@Override
	public <T> T eval(Rule rule, Object... dataMap) throws RuleException {
		return throwe();
	}

	
	private <T> T throwe()throws RuleException{
		throw new RuleException("The engine does not support!");
	}

	@Override
	public boolean verifySyntax(Rule rule) throws RuleException {
		return throwe();
	}
}
