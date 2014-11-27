package com.easyooo.framework.cache.console;

import java.util.List;


/**
 *
 * JVM Cache MBean，暴露JVM Cache的值
 *
 * @author Killer
 */
public interface CacheViewer {
	
	/**
	 * 获取MBean描述信息
	 * @return
	 */
	String getDescription();

	/**
	 * <p>返回JVM缓存的KEY，必须是一个真实存在缓存中的的cacheKey</p>
	 * 
	 * @param cacheKey
	 * @return 如果不存在返回null，存在返回一个实际的值，如果是一个实体，则是JSON字符串，
	 * 如果是一个分组，则返回的是一个数组格式的json。
	 */
	String get(String cacheKey);
	
	/**
	 *<p> 模糊查找JVM存在的主键值，需要一个标准的正则表达式，如：</p>
	 * <ul>
	 * 	<li><b><i>.*</i></b> 意味着查找所有存在的KEY，</li>
	 * 	<li><b><i>user.*</i></b> 查找所有user开头的KEY</li>
	 * </ul>
	 * <p>该操作比较影响性能，注意使用的频繁度</p>
	 * 
	 * @param cacheKeyPattern 缓存KEY的正则表达式
	 * @return
	 */
	List<String> keys(String cacheKeyPattern);
	
}
