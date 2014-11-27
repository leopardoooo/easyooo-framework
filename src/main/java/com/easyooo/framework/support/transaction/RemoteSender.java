package com.easyooo.framework.support.transaction;

/**
 * 发送器接口定义
 * 
 * @author Killer
 */
public interface RemoteSender<T> {

	/**
	 * 获取发送器提供商，用于DEBUG等
	 * @return
	 */
	public String getProviderInfo();
	
}
