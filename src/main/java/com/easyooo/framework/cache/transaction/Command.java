package com.easyooo.framework.cache.transaction;

import java.io.Serializable;

import lombok.ToString;

import com.easyooo.framework.cache.CacheLevel;


/**
 * 缓存命令的包装，同步或事物时非常有用
 * 
 * @author Killer
 */
@SuppressWarnings("serial")
@ToString
public class Command implements Serializable{
	
	private transient CacheLevel level;
	private Operation op;
	
	/** 缓存key */
	private String cacheKey;
	private String cacheValue;
	
	/** 分组KEY */
	private String cacheGroupKey;
	private String[] groupValues;
	
	/** 当操作是SETS时,keyvalues的缓存 */
	private String[] keyvalues;

	/**
	 * SET Command
	 */
	public static Command newSetCommand(String cacheKey, String value, CacheLevel level){
		return new Command(Operation.SET, cacheKey, value, level);
	}
	
	/**
	 * MOD Command
	 */
	public static Command newModCommand(String cacheKey, String value, CacheLevel level){
		return new Command(Operation.MOD, cacheKey, value, level);
	}
	
	/**
	 * DEL Command
	 */
	public static Command newDelCommand(String cacheKey, CacheLevel level){
		return new Command(Operation.DEL, cacheKey, level);
	}
	
	/**
	 * SETS Command
	 */
	public static Command newSetsCommand(String[] keyvalues, CacheLevel level){
		return new Command(Operation.SETS, keyvalues, level);
	}
	
	/**
	 * ADD_MEMBERS Command
	 */
	public static Command newAddMembersCommand(String groupKey, String[] entityCacheKeys, CacheLevel level){
		return new Command(Operation.ADD_MEMBERS, groupKey, entityCacheKeys, level);
	}
	
	/**
	 * DEL_MEMBERS Command
	 */
	public static Command newDelMembersCommand(String groupKey, String[] entityCacheKeys, CacheLevel level){
		return new Command(Operation.DEL_MEMBERS, groupKey, entityCacheKeys, level);
	}
	
	public Command(){}
	
	/**
	 * 单个缓存实体操作
	 */
	private Command(Operation op, String cacheKey, String cacheValue, CacheLevel level){
		this.op = op;
		this.cacheKey = cacheKey;
		this.cacheValue = cacheValue;
		this.level = level;
	}
	
	/**
	 * 删除操作命令
	 */
	private Command(Operation op, String cacheKey, CacheLevel level){
		this.op = op;
		this.cacheKey = cacheKey;
		this.level = level;
	}
	
	/**
	 * 多个缓存操作
	 */
	private Command(Operation op, String[] keyvalues, CacheLevel level){
		this.op = op;
		this.keyvalues = keyvalues;
		this.level = level;
	}
	
	/**
	 * 分组操作
	 */
	private Command(Operation op, String cacheGroupKey, String[] entityCacheKeys, CacheLevel level){
		this.op = op;
		this.cacheGroupKey = cacheGroupKey;
		this.groupValues = entityCacheKeys;
		this.level = level;
	}
	
	public CacheLevel getLevel() {
		return level;
	}

	public void setLevel(CacheLevel level) {
		this.level = level;
	}

	public Operation getOp() {
		return op;
	}

	public void setOp(Operation op) {
		this.op = op;
	}

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public String getCacheValue() {
		return cacheValue;
	}

	public void setCacheValue(String cacheValue) {
		this.cacheValue = cacheValue;
	}

	public String getCacheGroupKey() {
		return cacheGroupKey;
	}

	public void setCacheGroupKey(String cacheGroupKey) {
		this.cacheGroupKey = cacheGroupKey;
	}

	public String[] getGroupValues() {
		return groupValues;
	}

	public void setGroupValues(String[] groupValues) {
		this.groupValues = groupValues;
	}

	public String[] getKeyvalues() {
		return keyvalues;
	}

	public void setKeyvalues(String[] keyvalues) {
		this.keyvalues = keyvalues;
	}

	/**
	 * 接口操作命令
	 * @author Killer
	 */
	public enum Operation{
		
		/***
		 * 单个缓存的操作类
		 */
		SET,
		MOD,
		DEL,
		
		/**
		 * 一次设置多个key、values
		 */
		SETS,
		
		/** 
		 * 在同步JVM缓存的时候，会把ADD_MEMBERS、DEL_MEMBERS替换成该命令 
		 * 因为CacheLevel=JVM的情况，在添加获删除成员时，目标JVM没有获取所有的members的委托，
		 * 只能依靠数据库查询，而查询数据库是懒惰的，缓存是不会主动去查询数据库，只能等到Others来查
		 * 询该数据时，才会被赋值，因此同步JVM缓存的时候，如果Members是空，
		 * 则忽略ADD_MEMBERS、DEL_MEMBERS命令，等待下次查询时，一次性委托持久化层查询所有成员
		 * */
		ADD_MEMBERS,
		DEL_MEMBERS,
	}
	
}
