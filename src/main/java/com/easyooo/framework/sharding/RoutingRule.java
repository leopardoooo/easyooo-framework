package com.easyooo.framework.sharding;

/**
 * 
 * 路由规则定义
 * 
 * @author Killer
 */
public interface RoutingRule {

	
	/**
	 * 实现具体的路由根据当前的模块，上下文
	 * @param module
	 * @param context
	 * @return 返回数据源的Key
	 * @throws DsRoutingException
	 */
	DataSourceKey routing(Module module, RoutingContext context)throws DsRoutingException;
}
