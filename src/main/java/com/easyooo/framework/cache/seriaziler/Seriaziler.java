package com.easyooo.framework.cache.seriaziler;

import java.lang.reflect.Type;


/**
 *
 * 缓存类型转换器，因为缓存的Map仅支持字符串
 *
 * @author Killer
 */
public interface Seriaziler {

	/**
	 * 将对象序列化为字符串存入缓存服务器
	 * @param object
	 * @return
	 */
	public String seriazileAsString(Object object)throws SerializationException;

	/**
	 * 将字符串反序列化为对象
	 * @return
	 */
	public <T> T deserializeAsObject(String jsonString, Type type)throws SerializationException;
	
}
