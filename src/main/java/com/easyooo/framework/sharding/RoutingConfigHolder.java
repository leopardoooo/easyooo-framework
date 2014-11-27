package com.easyooo.framework.sharding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easyooo.framework.sharding.match.RegexMatcher;


/**
 * 数据源配置信息缓存
 *
 * @author Killer
 */
public final class RoutingConfigHolder {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static RoutingConfigHolder instance = new RoutingConfigHolder();
	
	private Map<Module, List<RoutingGroup>> routingMap = new ConcurrentHashMap<>();
	
	private Map<Module, List<Matcher>> moduleMapping = new ConcurrentHashMap<>();
	
	private volatile boolean loaded;
	private volatile boolean keyLoaded;
	
	private List<DataSourceKey> dataSourceKeys = new ArrayList<>(); 
	
	private RoutingConfigHolder(){
	}
	
	public static RoutingConfigHolder getInstance(){
		return instance;
	}
	
	/**
	 * 初始化数据源配置
	 * @param groups
	 */
	public synchronized void initialize(List<RoutingGroup> groups, 
			List<ModuleMapping> mapping){
		if(groups == null || mapping == null)
			return;
		// put list
		initGroups(groups);
		
		// init mapping
		initMapping(mapping);
		
		loaded = true;
		
		// 
		printRoutingInfo();
	}
	
	public synchronized void initialize(List<DataSourceKey> keys){
		if(keys == null)
			return;
		this.dataSourceKeys = keys;
		keyLoaded = true;
	}
	
	private void initGroups(List<RoutingGroup> groups){
		for (RoutingGroup hg : groups) {
			List<RoutingGroup> hgList = routingMap.get(hg.getModule());
			if(hgList == null){
				hgList = new Vector<RoutingGroup>();
				routingMap.put(hg.getModule(), hgList);
			}
			hgList.add(hg);
		}
		
		//按区间排序，并填充区间值
		Set<Module> keySet = routingMap.keySet();
		for (Module module : keySet) {
			new RoutingGroupUtil(routingMap.get(module)).orderAndRange();
		}
		
	}
	
	private void initMapping(List<ModuleMapping> mapping){
		for (ModuleMapping mm : mapping) {
			List<Matcher> mmList = moduleMapping.get(mm.getModule());
			if(mmList == null){
				mmList = new Vector<Matcher>();
				moduleMapping.put(mm.getModule(), mmList);
			}
			
			for (String deformityRegex : mm.getMatchTextList()) {
				mmList.add(new RegexMatcher(deformityRegex));
			}
		}
	}
	
	public List<RoutingGroup> getRoutingGroup(Module module){
		if(loaded){
			return routingMap.get(module);
		}
		throwInitException();
		return null;
	}
	
	public Set<Module> getModules(){
		if(loaded){
			return moduleMapping.keySet();
		}
		throwInitException();
		return null;
	}
	
	public List<DataSourceKey> getDataSourceKeys(){
		if(keyLoaded){
			return dataSourceKeys;
		}
		throwInitException();
		return null;
	}
	
	public List<Matcher> getModuleMapping(Module module){
		if(loaded){
			return moduleMapping.get(module);
		}
		throwInitException();
		return null;
	}
	
	private void throwInitException(){
		throw new DsRoutingException("The Cache Data is not initialized!");
	}
	
	private void printRoutingInfo(){
		if(logger.isInfoEnabled()){
			Set<Module> keySet = routingMap.keySet();
			StringBuffer buffer = new StringBuffer("{");
			int counter = 0;
			for (Module module : keySet) {
				buffer.append(module.getName());
				buffer.append("-routing: [");
				
				List<RoutingGroup> groups = routingMap.get(module);
				for (RoutingGroup rg : groups) {
					for (DataSourceKey dsk : rg.getDataSourceKeys()) {
						buffer.append(dsk.getKey());
						buffer.append(", ");
					}
					NumberRange nr = rg.getRange();
					buffer.append(nr.getMin() + " - " + nr.getMax());
					if(groups.size() > 1){
						buffer.append("\n");
					}
				}
				buffer.append("]");
				if(++counter < keySet.size()){
					buffer.append(",");
				}
			}
			buffer.append(" }");
			logger.info(buffer.toString());
		}
	}
}
