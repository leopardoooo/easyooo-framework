package com.easyooo.framework.sharding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.easyooo.framework.sharding.routing.RangeRoutingRule;
import com.easyooo.framework.sharding.transaction.DelegatingDataSource;

/**
 * 数据源路由，具体的路由规则由<code>RoutingRule</code>接口实现
 *
 * @see RoutingRule
 * 
 * @author Killer
 */
public class ModulusRoutingDataSource extends AbstractRoutingDataSource
		implements ApplicationContextAware {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	private ModuleMatcher matcher = new ModuleMatcher();
	
	private RoutingRule defaultRoutingRule = new RangeRoutingRule();
	
	private Map<Module, RoutingRule> moduleRules;
	
	private ApplicationContext applicationContext;
	
	@Override
	public void afterPropertiesSet() {
		List<DataSourceKey> dskeys = RoutingConfigHolder.getInstance().getDataSourceKeys();
		
		Map<Object, Object> targetDataSources = new HashMap<>(dskeys.size()); 
		for (DataSourceKey dsk : dskeys) {
			DataSource ds = new DelegatingDataSource(applicationContext
					.getBean(dsk.getKey(), DataSource.class));
			targetDataSources.put(dsk, ds);
		}
		setTargetDataSources(targetDataSources);
		super.afterPropertiesSet();
		// setDefaultTargetDataSource(null); 
	}
	
	@Override
	protected Object determineCurrentLookupKey() {
		RoutingContext context = RoutingContextHolder.getRoutingContext();
		
		if(context == null){
			throw new DsRoutingException(
					" No binding data source routing context parameter.");
		}
		
		Module module = getMatchModule(context);
		
		if(module == null){
			throw new DsRoutingException(context + 
					" Can't find the mapping module.");
		}
		
		DataSourceKey dsk = null;
		if(moduleRules != null && moduleRules.containsKey(module)){
			dsk = moduleRules.get(module).routing(module, context);
		}else{
			dsk = defaultRoutingRule.routing(module, context);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("The dataSource is redirected to " + dsk.getKey());
		}
		
		return dsk;
	}
	
	private Module getMatchModule(RoutingContext context){
		RoutingConfigHolder rch = RoutingConfigHolder.getInstance();
		Set<Module> modules = rch.getModules();
		
		for (Module module : modules) {
			if(matcher.isMatch(context, module)){
				return module;
			}
		}
		return null;
	}

	public void setModuleRules(Map<Module, RoutingRule> moduleRules) {
		this.moduleRules = moduleRules;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
		
	}
	

}
