package com.easyooo.framework.support.transaction;

import java.util.Collection;

/**
 * 批量发送
 *
 * @author Killer
 */
public interface RemoteSenderMorly<T> extends RemoteSender<T>{
	
	/**
	 * 批量发送接口方法定义
	 */
	boolean send(Collection<T> data)throws Exception;

}
