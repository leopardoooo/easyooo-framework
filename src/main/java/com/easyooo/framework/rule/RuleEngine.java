package com.easyooo.framework.rule;

/**
 *<p>规则引擎接口定义，该接口中定义了执行规则所需要的方法， 具体规则语法及执行策略需要子类完成</p>
 *<p>
 * 在脚本中可以访问global、local两个变量根引用，global来自{@link #setContext(RuleContext)}的参数，
 * 而local也可以是RuleContext，但也可以是在调用{@link #eval(Rule, Object...)}时指定。
 *</P>
 *
 * @author Killer
 */
public interface RuleEngine {
	
	/**
	 * 执行规则之前，可为引擎设置全局上下文参数，
	 * 全局上下文参数时针对引擎实例来说的，
	 * 同一个引擎实例在任何一次执行都可以获取到上下文参数，
	 * 相比调用eval所传入的局部变量，生命周期更长
	 * 
	 * @param context
	 */
	void setContext(RuleContext context);
	
	
	/**
	 * 根据Rule定义执行脚本，脚本支持多种引擎 
	 * @see Language
	 * 
	 * 执行的脚本内容决定是否依赖于上下文，返回值也依赖于脚本内容
	 * 如果脚本内容无返回值，则返回null
	 * @param rule
	 * @return  
	 * @throws RuleException
	 */
	<T> T eval(Rule rule)throws RuleException;
	
	
	/**
	 * 执行脚本，可传入局部变量，局部变量执行完成将会自动清理
	 * 
	 * @param rule
	 * @param dataMap 局部变量表，如：eval(rule, "gift", gift, "goods", goods)
	 * @return
	 * @throws RuleException
	 */
	<T> T eval(Rule rule, Object... dataMap)throws RuleException;
	
}
