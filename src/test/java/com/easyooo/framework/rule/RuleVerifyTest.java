/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.rule;

import org.junit.Test;

/**
 *
 * @author Killer
 */
public class RuleVerifyTest {

	String classRuleText = "int a = local.get(\"a\"); System.out.println(\"a=\" + a);"
			+ " if(a > 18 && bv < 36){ return \"美女\";} else{ return \"滚\"; }";
	
	/**
	 * 简单的测试一段Java脚本，应该重新编译新的版本
	 * @throws RuleException
	 */
	@Test
	public void testJavaEngine() throws RuleException{
		Rule rule = RuleBuilder.build("1", classRuleText, Language.JAVA, 1);
		new RuleEngineProxy(rule).verifySyntax();
		System.out.println(new RuleEngineProxy(rule).eval("a" , 30));;
	}
	
}
