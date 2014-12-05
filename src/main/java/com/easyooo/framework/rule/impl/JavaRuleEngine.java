package com.easyooo.framework.rule.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easyooo.framework.common.util.MapUtil;
import com.easyooo.framework.rule.Rule;
import com.easyooo.framework.rule.RuleContext;
import com.easyooo.framework.rule.RuleEngine;
import com.easyooo.framework.rule.RuleException;
import com.easyooo.framework.rule.RuleExecutor;

/**
 *The Rule for java class code
 * @author Killer
 */
public class JavaRuleEngine implements RuleEngine {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private RuleContext ruleContext;
	
	public JavaRuleEngine(){
	}
	
	@Override
	public <T> T eval(Rule rule) throws RuleException {
		return __eval(rule, MapUtil.gmap());
	}

	@Override
	public <T> T eval(Rule rule, Object... dataMap) throws RuleException {
		if(dataMap.length == 1 && dataMap[0] == null){
			return eval(rule);
		}
		return __eval(rule, MapUtil.gmap(dataMap));
	}
	
	@SuppressWarnings("unchecked")
	public <T> T __eval(Rule rule, Map<String, Object> local)throws RuleException{
		RuleExecutor re = RuleClassManager.getInstance().get(rule);
		Object o = re.eval(ruleContext, new RuleContext(local));
		if(o == null){
			return null;
		}
		return (T) o;
	}
	
	@Override
	public void setContext(RuleContext context) {
		this.ruleContext = context;
	}

	@Override
	public boolean verifySyntax(Rule rule) throws RuleException {
		return RuleClassManager.getInstance().verifyRuleSyntax(rule);
	}

}
