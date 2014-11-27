package com.easyooo.framework.rule.js;

import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import com.easyooo.framework.common.util.MapUtil;
import com.easyooo.framework.rule.Names;
import com.easyooo.framework.rule.Rule;
import com.easyooo.framework.rule.RuleContext;
import com.easyooo.framework.rule.RuleEngine;
import com.easyooo.framework.rule.RuleException;


/**
 * JavaScript 引擎实现, 依赖java默认js的引擎<code>rhino</code>
 * 
 * @author Killer
 */
public class JavaScriptRuleEngine implements RuleEngine {

	private static ScriptEngine scriptEngine;
	private RuleContext context;
	
	public JavaScriptRuleEngine(){
		ScriptEngineManager engineManager = new ScriptEngineManager();
        scriptEngine = engineManager.getEngineByName("js");
	}
	
	@Override
	public void setContext(RuleContext context) {
		this.context = context;
	}

	@Override
	public <T> T eval(Rule rule) throws RuleException {
		return _eval(rule.getRuleText(), MapUtil.gmap());
	}

	@Override
	public <T> T eval(Rule rule, Object... dataMap) throws RuleException {
		if(dataMap.length == 1 && dataMap[0] == null){
			return eval(rule);
		}
		return _eval(rule.getRuleText(), MapUtil.gmap(dataMap));
	}
	
	@SuppressWarnings("unchecked")
	private <T> T _eval(String scriptText, Map<String, Object> data)throws RuleException{
		Bindings bindings = new SimpleBindings();
		if(context != null){
			bindings.put(Names.GLOBAL, context);
		}
		bindings.put(Names.LOCAL, data);
		
		// execution
		try {
			Object o = scriptEngine.eval(scriptText, bindings);
			
			if(o != null){
				return (T)o;
			}
			return null;
		} catch (ScriptException e) {
			throw new RuleException("Execute the script error", e);
		}
	}
}
