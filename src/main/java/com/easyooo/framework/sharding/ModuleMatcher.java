package com.easyooo.framework.sharding;

import java.util.List;

/**
 * 模块匹配器
 *
 * @author Killer
 */
public class ModuleMatcher {

	/**
	 * 检查当前上下文是否传入的模块
	 * 
	 * @param context
	 * @param module
	 * @return
	 */
	public boolean isMatch(RoutingContext context, Module module){
		if(module == null){
			return false;
		}
		List<Matcher> matchList = RoutingConfigHolder.getInstance()
				.getModuleMapping(module);
		
		for (Matcher matcher : matchList) {
			if(matcher.isMatch(context.getTable())){
				return true;
			}
		}
		
		return false;
	}
	
}
