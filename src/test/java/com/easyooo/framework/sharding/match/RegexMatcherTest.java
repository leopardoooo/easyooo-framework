package com.easyooo.framework.sharding.match;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.easyooo.framework.sharding.Matcher;

/**
 * 匹配器测试
 *
 * @author Killer
 */
public class RegexMatcherTest {

	@Test
	public void testIsMatch(){
		Matcher matcher = new RegexMatcher("t_.*");
		assertThat(matcher.isMatch("T_USER"), is(true));
		assertThat(matcher.isMatch("T_User"), is(true));
		assertThat(matcher.isMatch("t_user"), is(true));
	}
	
}
