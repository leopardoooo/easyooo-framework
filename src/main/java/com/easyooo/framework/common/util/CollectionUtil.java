package com.easyooo.framework.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Killer
 */
public final class CollectionUtil {

	
	/**
	 * 根据给定的元素创建List， 返回的List是一个非安全的集合
	 * 
	 * @see #addAll(List, Object...)
	 * @param args 元素
	 * @return
	 */
	@SafeVarargs
	public static <T> List<T> gList(T...args){
		List<T> target = new ArrayList<T>(args.length);
		return addAll(target, args);
	}
	
	/**
	 * 追加可变元素 
	 * 需要注意的是可变参数不允许传入一个多种类型的数组，否则运行出现CCE
	 * @param src
	 * @param args 
	 * @return 
	 */
	@SafeVarargs
	public static <T> List<T> addAll(List<T> src, T...args){
		if(src == null){
			return null;
		}
		
		for (T t : args) {
			src.add(t);
		}
		
		return src;
	}
	
}
