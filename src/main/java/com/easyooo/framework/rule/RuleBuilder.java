package com.easyooo.framework.rule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.easyooo.framework.rule.impl.SimpleRule;

/**
 * 规则生成器
 * 
 * @author Killer
 */
public final class RuleBuilder {

	static final String PREFFIX = "Auto";
	static volatile int counter = 1;
	static final Language DEFAULT_LANGUAGE = Language.JAVA;
	static final Integer DEFAULT_VERSION = 1;
	
	private static String getId(){
		return PREFFIX + incrementGet();
	}
	
	public static Rule build(String ruleText){
		return build(getId(), ruleText, DEFAULT_LANGUAGE, DEFAULT_VERSION);
	}
	
	public static Rule build(String ruleText, Integer version ){
		return build(getId(), ruleText, DEFAULT_LANGUAGE, version);
	}
	
	public static Rule build(String ruleId , String ruleText, Language language, Integer version){
		return new SimpleRule(ruleId, ruleText, language, version);
	}
	
	public static Rule build(Reader reader)throws RuleException{
		return build(reader, DEFAULT_LANGUAGE, DEFAULT_VERSION);
	}
	
	
	public static Rule build(String ruleText, Language ruleType)throws RuleException{
		return build(new StringReader(ruleText), ruleType, DEFAULT_VERSION);
	}
	public static Rule build(Reader reader, Language ruleType)throws RuleException{
		return build(reader, ruleType, DEFAULT_VERSION);
	}
	
	public static Rule build(Reader reader, Language ruleType, Integer version)throws RuleException{
		BufferedReader br = null;
		try{
			br = new BufferedReader(reader);
			StringBuffer sb = new StringBuffer();
			String line = null;
			while((line = br.readLine()) != null){
				sb.append( line );
			}
			return build(getId(), sb.toString(), ruleType, version);
		}catch(IOException e){
			throw new RuleException(e);
		}finally{
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public static int incrementGet(){
		return counter ++;
	}
	
}
