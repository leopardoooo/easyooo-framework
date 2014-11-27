package com.easyooo.framework.rule;


import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * <p>规则引擎单元测试</p>
 * <p>
 * 测试结果:
 * <ul>
 * <li>java规则引擎是js引擎的24倍左右</li>
 * <li>java规则引擎是groovy引擎的3倍左右</li>
 * </UL>
 * <b>可以看出java来实现规则引擎是比较合适的</b>
 * </p>
 * <p>
 * <b><i>NOTE</i></b>
 * groovy生成的字节码比java规则生成的字节码多了近10倍，而且内部用了反射机制，
 * 因此同样是class文件，执行时间却相差几倍的原因，当然Groovy还可以进一步优化，
 * 可以使得生成的字节码通过ClassVisitor进行修改。
 * </p>
 * @author Killer
 */
public class RuleEngineManagerTest2 {

	String classRuleText = "int a = local.get(\"a\");"
			+ " if(a > 18 && a < 36){ return \"美女\";} else{ return \"滚\"; }";
	
	String scriptRuleText = "var a = local.get('a'); "
			+ " if(a > 18 && a < 36){ '美女'} else{ '滚' }";
	
	// 全局上下文
	Map<String, Object> context = null;
	
	// 循环的次数
	static final int testCount = 2000; 
	
	@Before
	public void setUp(){
		context = new HashMap<String, Object>();
	}
	
	@BeforeClass
	public static void common(){
		System.out.println("Test " + testCount + " times the total time consuming: ");
	}
	
	/**
	 * 简单的测试一段Java脚本
	 * @throws RuleException
	 */
	@Test
	public void testJavaEngine() throws RuleException{
		StringReader sr = new StringReader(classRuleText);
		Rule rule = RuleBuilder.build(sr, Language.JAVA);
		RuleEngineProxy proxy = new RuleEngineProxy(rule);
		evalAndAssert("java", proxy);
	}
	
	/**
	 * Test Groovy Engine
	 */
	@Test
	@Ignore
	public void testGroovyEngine()throws RuleException{
		StringReader sr = new StringReader(classRuleText);
		Rule rule = RuleBuilder.build(sr, Language.GROOVY);
		RuleEngineProxy proxy = new RuleEngineProxy(rule);
		evalAndAssert("groovy", proxy);
	}
	
	
	/**
	 * Test JavaScript Engine
	 * @throws RuleException
	 */
	@Test
	@Ignore
	public void testJavaScriptEngine()throws RuleException{
		StringReader sr = new StringReader(scriptRuleText);
		Rule rule = RuleBuilder.build(sr, Language.JAVASCRIPT);
		RuleEngineProxy proxy = new RuleEngineProxy(rule);
		evalAndAssert("rhino", proxy);
	}
	
	/**
	 * eval rule and assert return
	 * @param proxy
	 * @throws RuleException
	 */
	private void evalAndAssert(String tag, RuleEngineProxy proxy)throws RuleException{
		long start = System.currentTimeMillis();
		for(int i = 0; i < testCount; i++){
			String rv = proxy.eval("a", 24);
			assertThat(rv, is("美女"));
		}
		System.out.println(tag + " : " + (System.currentTimeMillis() - start) + "ms");
	}
}
