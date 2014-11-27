package com.easyooo.framework.rule;


/**
 * 脚本加载器
 * 
 * @author Killer
 */
public interface RuleClassLoader {
	
	/**
	 * 将规则加载到内存
	 * @param rule
	 * @return
	 * @throws RuleException
	 */
	Class<?> loadClass(Rule rule)throws RuleException;
	
}
