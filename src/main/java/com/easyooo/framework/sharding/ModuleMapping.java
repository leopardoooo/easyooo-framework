package com.easyooo.framework.sharding;

import java.util.List;

/**
 * 模块与表之间的映射关系
 * @author Killer
 */
public class ModuleMapping {

	private Module module;
	private List<String> matchTextList;
	
	public ModuleMapping(){
	}
	
	public ModuleMapping(Module module, List<String> matchTextList) {
		super();
		this.module = module;
		this.matchTextList = matchTextList;
	}
	public Module getModule() {
		return module;
	}
	public void setModule(Module module) {
		this.module = module;
	}
	public List<String> getMatchTextList() {
		return matchTextList;
	}
	public void setMatchTextList(List<String> matchList) {
		this.matchTextList = matchList;
	}
}
