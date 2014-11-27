/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache;

/**
 * 实现该接口完成数据持久化操作
 * 
 * @author Killer
 */
public interface Delegater<T> {

	T execute()throws DataProxyException;
}
