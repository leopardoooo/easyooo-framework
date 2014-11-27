package com.easyooo.framework.cache.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.easyooo.framework.cache.config.GroupBean;

/**
 *
 * @author Killer
 */
public class CascadeOperation {
	
	public enum Operation{
		/** 删除旧组,插入新组 */
		DELETE_INSERT,
		/** 当新值为NULL, 仅删除旧组  */
		DELETE,
		/** 当就值为NULL, 仅插入新组 */
		INSERT,
		/** 不做级联  */
		NOTHING
	}
	
	private String group;
	private List<GroupBean> groupList;
	private Operation operation;
	
	public CascadeOperation(String group, List<GroupBean> groupList,
			Operation operation) {
		this.group = group;
		this.groupList = groupList;
		this.operation = operation;
	}
	
	/**
	 * 获取新增级联操作
	 * @param groups
	 * @return
	 */
	public static List<CascadeOperation> getInsertOps(
			Map<String, List<GroupBean>> groups) {
		List<CascadeOperation> target = new ArrayList<>();
		for (Map.Entry<String, List<GroupBean>> entry : groups.entrySet()) {
			target.add(new CascadeOperation(entry.getKey(), entry.getValue(),
					Operation.INSERT));
		}
		return target;
	}
	
	/**
	 * 获取删除级联操作
	 * @param groups
	 * @return
	 */
	public static List<CascadeOperation> getDeleteOps(
			Map<String, List<GroupBean>> groups) {
		List<CascadeOperation> target = new ArrayList<>();
		for (Map.Entry<String, List<GroupBean>> entry : groups.entrySet()) {
			target.add(new CascadeOperation(entry.getKey(), entry.getValue(),
					Operation.DELETE));
		}
		return target;
	}
	
	/**
	 * 获取修改级联操作
	 */
	public static List<CascadeOperation> getUpdateOps(
			Map<String, List<GroupBean>> groups, Map<String, Object> oldMap,
			Map<String, Object> newMap, boolean ignoreNullValue) {
		List<CascadeOperation> target = new ArrayList<>();
		// loop start
		for (Map.Entry<String, List<GroupBean>> entry : groups.entrySet()) {
			List<GroupBean> groupList = entry.getValue();
			
			// local operation val
			Operation operation = Operation.NOTHING;
			for (GroupBean bean : groupList) {
				String name = bean.getPropertyName();
				Object newValue = newMap.get(name);
				Object oldValue = oldMap.get(name);
				
				// selective mode and ignore null
				if(ignoreNullValue && newValue == null){
					continue;
				}
				
				// compare and set operation 
				if(newValue == null && oldValue != null){
					operation = Operation.DELETE;
				}else if(newValue != null && oldValue == null){
					operation = Operation.INSERT;
				}else if(newValue == null && oldValue == null){
					operation = Operation.NOTHING;
				}else if(!newValue.equals(oldValue)){
					operation = Operation.DELETE_INSERT;
				}else{
					continue;
				}
				break;
			}
			if(Operation.NOTHING != operation){
				target.add(new CascadeOperation(entry.getKey(), entry
						.getValue(), operation));
			}
		}
		// loop end 
		return target;
	}

	public String getGroup() {
		return group;
	}

	public List<GroupBean> getGroupList() {
		return groupList;
	}

	public Operation getOperation() {
		return operation;
	}
}
