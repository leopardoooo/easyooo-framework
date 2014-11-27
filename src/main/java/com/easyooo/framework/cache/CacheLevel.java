/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache;

/**
 * 
 * 缓存策略枚举定义
 * 
 * @author Killer
 */
public enum CacheLevel {

	/**
	 * 仅支持缓存到本地JVM中
	 */
	JVM,
	
	/**
	 * 仅缓存到远程服务器上
	 */
	REDIS,
	
	/**
	 * 支持委托式缓存，本地与远程服务器都将缓存，
	 * 并且本地的缓存优先级永远比远程的高
	 */
	JVM_TO_REDIS
	
}
