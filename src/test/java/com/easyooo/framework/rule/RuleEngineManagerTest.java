package com.easyooo.framework.rule;


import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringReader;

import org.junit.Test;

public class RuleEngineManagerTest {

	String classRuleText = "int a = local.get(\"a\");"
			+ " if(a > 18 && a < 36){ return \"美女\";} else{ return \"滚\"; }";
	
	String scriptRuleText = "var a = local.get('a'); "
			+ " if(a > 18 && a < 36){ '美女'} else{ '滚' }";
	
	/**
	 * 简单的测试一段Java脚本
	 * @throws RuleException
	 */
	@Test
	public void testJavaEngine() throws RuleException{
		Rule rule = RuleBuilder.build(new StringReader(classRuleText), Language.JAVA);
		RuleEngineProxy proxy = new RuleEngineProxy(rule);
		try{
			Object obj = null;
			proxy.eval(obj);
		}catch(NullPointerException e){
			//e.printStackTrace();
		}
		String rv = proxy.eval("a", 24);
		assertThat(rv, is("美女"));
		
		Rule rule1 = RuleBuilder.build(new StringReader(classRuleText), Language.JAVASCRIPT);
		RuleEngineProxy proxy1 = new RuleEngineProxy(rule1);
		
		try{
			Object obj = null;
			proxy1.eval(obj);
		}catch(NullPointerException e){
			//e.printStackTrace();
		}
		String rv1 = proxy.eval("a", 24);
		assertThat(rv1, is("美女"));
	}
}
