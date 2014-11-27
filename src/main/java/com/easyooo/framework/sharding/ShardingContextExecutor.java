package com.easyooo.framework.sharding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easyooo.framework.sharding.annotation.Table;

/**
 * 路由规则绑定执行器
 * @author Killer
 */
public class ShardingContextExecutor {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private boolean forceUseRouting = true;
	
	/**
	 * SQL执行之前需要该代理配置路由信息
	 * 
	 * @param args SQL执行的参数，需要使用该参数确定水平切分候选参数,如果是一个 {@link ShardingStrategy}
	 * @param table Table 表名注解配置
	 * @param callback 
	 * @throws Throwable
	 */
	public Object doProxy(Object paramObj, Table table, ShardingCallback callback)throws Throwable{
		return doProxy(getKeyword(new Object[]{paramObj}), table, callback);
	}
	
	/**
	 * <p>当多个参数不确定路由规则的时候可以使用该方法传入一组参数，
	 * 方法会从0开始找对应{@link #ShardingContextExecutor()}的参数 </p>
	 */
	public Object doProxy(Object[] paramArgs, Table table, ShardingCallback callback)throws Throwable{
		return doProxy(getKeyword(paramArgs), table, callback);
	}
	
	public Object doProxy(Long keyword, Table table, ShardingCallback callback)throws Throwable{
		if(logger.isDebugEnabled()){
			if(keyword == null){
				logger.debug("Didn't find keyword.");
			}
		}
		
		if(table == null){
			String warn = String.format("Not found sharding configuration information.");
			if(forceUseRouting){
				throw new DsRoutingException(warn);
			}else{
				logger.warn(warn);
			}
			return callback.doCallback();
		}
		
		RoutingContext context = new RoutingContext();
		context.setKeyword(keyword);
		context.setTable(table.value());
		
		try{
			// Put context 
			RoutingContextHolder.setRoutingContext(context);
			return callback.doCallback();
		}finally{
			// Clear the context
			RoutingContextHolder.clearRoutingContext();
		}
	}
	
	protected Long getKeyword(Object[] args){
		if(args == null)
			return null;
		for (Object arg : args) {
			if(arg != null && arg instanceof ShardingStrategy){
				ShardingStrategy ss = (ShardingStrategy)arg;
				return ss.getShardingKeyword();
			}
		}
		return null;
	}

	public void setForceUseRouting(boolean forceUseRouting) {
		this.forceUseRouting = forceUseRouting;
	}
}
