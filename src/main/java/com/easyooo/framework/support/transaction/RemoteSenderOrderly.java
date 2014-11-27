package com.easyooo.framework.support.transaction;

/**
 * 消息发送器接口定义
 *
 * @author Killer
 */
public interface RemoteSenderOrderly<T> extends RemoteSender<T>{

	/**
	 * 发送字节码数组
	 * 
	 * @param data
	 * @return 如果成功返回true,失败返回 false
	 * @throws Throwable
	 */
	boolean send(T data)throws Exception;
	
	
}
