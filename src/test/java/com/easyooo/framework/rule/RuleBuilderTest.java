package com.easyooo.framework.rule;


import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 *
 * @author Killer
 */
public class RuleBuilderTest {

	@Test
	public void testBuild1(){
		String ruleText = "if(age > 20){ return 0;}else{ return 1;}";
		Rule rule = RuleBuilder.build(ruleText, 1);
		
		assertThat(rule, hasProperty("ruleId", is("Auto1")));
		assertThat(rule, hasProperty("ruleText", is(ruleText)));
		assertThat(rule, hasProperty("language", is(Language.JAVA)));
		assertThat(rule, hasProperty("version", is(1)));
	}
	
	@Test
	public void testBuild2()throws Throwable{
		InputStreamReader isr = null;
		InputStreamReader isr2 = null;
		try{
			isr = new InputStreamReader(getClass().getResourceAsStream("test.rule"));
			isr2 = new InputStreamReader(getClass().getResourceAsStream("test.rule"));
			List<String> lines = IOUtils.readLines(isr2);
			
			StringBuffer sb = new StringBuffer();
			for (String string : lines) {
				sb.append(string);
			}
			
			Rule rule = RuleBuilder.build(isr, Language.JAVA, 1);
			
			assertThat(rule, hasProperty("ruleId", is("Auto2")));
			assertThat(rule, hasProperty("ruleText", is(sb.toString())));
			assertThat(rule, hasProperty("language", is(Language.JAVA)));
			assertThat(rule, hasProperty("version", is(1)));
		}finally{
			isr.close();
			isr2.close();
		}
	}
	
}
