package com.easyooo.framework.sharding.match;

import java.util.regex.Pattern;

import com.easyooo.framework.sharding.Matcher;

/**
 * 正则匹配器
 *
 * @author Killer
 */
public class RegexMatcher implements Matcher{
	
	private String deformityRegex;
	private Pattern pattern;
	
	public RegexMatcher(String deformityRegex){
		this.deformityRegex = deformityRegex;
		this.pattern = Pattern.compile("^" + this.deformityRegex + "$",
				Pattern.CASE_INSENSITIVE); 
	}
 
	@Override
	public boolean isMatch(String input) {
		return pattern.matcher(input).matches();
	}

	public String getDeformityRegex() {
		return deformityRegex;
	}
}
