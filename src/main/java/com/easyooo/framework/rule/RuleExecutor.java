package com.easyooo.framework.rule;



/**
 *规则执行器接口定义，所有的脚本内容都会间接实现该类，动态编译成Class字节码文件，
 *在执行的过程中脚本引擎将利用Java多态的特性执行接口函数
 *
 *除了<code>Rhino</code> 实现的JS Engine，它不会实现该接口，
 *而是直接以脚本的方式执行
 *
 * @author Killer
 */
public interface RuleExecutor {

	
	/**
	 * 在脚本中都可以使用global以及local两个内置参数引用，
	 * 具体的值依赖于系统业务实现，在脚本中使用的过程中，global与local的作用是一致的。
	 * 只是分别属于两个作用域的参数，与<code>Http Request</code>
	 * <code>Http Session</code>有着异曲同工之处
	 * 
	 * <p> 在脚本中可直接使用: </p>
	 * <ul>
	 * 	<li>global.get("xxx")</li>
	 * 	<li>local.get("xxx")</li>
	 * </ul>
	 * 
	 * 这种用法取决于脚本引擎的实现
	 * 在<b>Rhino</b>引擎中，可以直接使用如 <code>global.xxx</code>
	 * 
	 * @param global 
	 * @param local
	 * @return
	 * @throws RuleException
	 */
	Object eval(RuleContext global, RuleContext local)throws RuleException;
	
}
