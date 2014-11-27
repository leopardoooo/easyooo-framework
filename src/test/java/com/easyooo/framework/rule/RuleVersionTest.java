/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.rule;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 *
 * @author Killer
 */
public class RuleVersionTest {

	String classRuleText = "int a = local.get(\"a\"); System.out.println(\"a=\" + a);"
			+ " if(a > 18 && a < 36){ return \"美女\";} else{ return \"滚\"; }";
	
	/**
	 * 简单的测试一段Java脚本，应该重新编译新的版本
	 * @throws RuleException
	 */
	@Test
	public void testJavaEngine() throws RuleException{
		Rule rule = RuleBuilder.build("1", classRuleText, Language.JAVA, 1);
		String rv = new RuleEngineProxy(rule).eval("a", 24);
		
		Rule rule1 = RuleBuilder.build("1", classRuleText , Language.JAVA, 2);
		String rv1 = new RuleEngineProxy(rule1).eval("a", 24);
		
		Rule rule2 = RuleBuilder.build("1", classRuleText , Language.JAVA, 2);
		String rv2 = new RuleEngineProxy(rule2).eval("a", 12);
		
		Rule rule3 = RuleBuilder.build("1", classRuleText , Language.JAVA, 1);
		String rv3 = new RuleEngineProxy(rule3).eval("a", 12);
		
		assertThat(rv, is("美女"));
		assertThat(rv1, is("美女"));
		assertThat(rv2, is("滚"));
		assertThat(rv3, is("滚"));
	}
	
}
