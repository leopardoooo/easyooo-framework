package com.easyooo.framework.sharding.routing;

import java.util.List;

import com.easyooo.framework.sharding.DataSourceKey;
import com.easyooo.framework.sharding.DsRoutingException;
import com.easyooo.framework.sharding.Module;
import com.easyooo.framework.sharding.NumberRange;
import com.easyooo.framework.sharding.RoutingConfigHolder;
import com.easyooo.framework.sharding.RoutingContext;
import com.easyooo.framework.sharding.RoutingGroup;
import com.easyooo.framework.sharding.RoutingRule;

/**
 * 根据分组进行取模数。
 * 将分组按照区间进行匹配，匹配上了在根据节点数量进行取模数
 *
 * @author Killer
 */
public class RangeRoutingRule implements RoutingRule {

	@Override
	public DataSourceKey routing(Module module, RoutingContext context) throws DsRoutingException {
		Long keyword = context.getKeyword();

		// 查找对应的路由组
		RoutingGroup group = findRoutingGroup(module, keyword);
		if(group == null){
			DsRoutingException dre = new DsRoutingException(module 
					+ " can't locate the datasource, Because there is no group");
			dre.setKeyword(keyword);
			dre.setModule(module);
			throw dre;
		}
		
		List<DataSourceKey> dsk = group.getDataSourceKeys();
		if(dsk.size() == 1){
			return dsk.get(0);
		}
		
		// 存在多个数据源，检查Keyword
		checkKeyword(module, keyword);
		
		long modulus = keyword % dsk.size();
		return dsk.get(new Long(modulus).intValue());
	}
	
	private void checkKeyword(Module module, Long keyword){
		if(keyword == null){
			DsRoutingException dre = new DsRoutingException(
					module + " is configured with multiple groups, But the keyword is null.");
			dre.setModule(module);
			throw dre;
		}
	}
	
	protected List<RoutingGroup> getRoutingGroups(Module module){
		RoutingConfigHolder holder = RoutingConfigHolder.getInstance();
		return holder.getRoutingGroup(module);
	}
	
	/**
	 * 如果只有一个Group，则直接返回，
	 * 否则按照NumberRange区间与Keyword进行匹配，如果在属于Group的区间值则匹配成功
	 * 
	 * @param groups
	 * @param keyword
	 * @return
	 */
	protected RoutingGroup findRoutingGroup(Module module, Long keyword){
		List<RoutingGroup> groups = getRoutingGroups(module);
		
		if(groups.size() == 1){
			return groups.get(0);
		}
		
		// 多个Group，检查keyword
		checkKeyword(module, keyword);
		
		for (RoutingGroup group : groups) {
			NumberRange range = group.getRange();
			
			if(keyword >= range.getMin().longValue()
					&& keyword <= range.getMax().longValue()){
				return group;
			}
		}
		
		return null;
	}

}
