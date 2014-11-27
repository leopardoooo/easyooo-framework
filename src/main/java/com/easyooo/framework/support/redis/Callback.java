package com.easyooo.framework.support.redis;

/**
 * 回调接口定义，它是一种模式，将一个完整的业务流程进行拆分，
 * 由回调接口完成外部不确定的流程
 * 通常与Template组合使用
 *
 * @param P 回调的参数泛型定义
 * @param V 返回值
 * @author Killer
 */
public interface Callback<P,V> {

	
	/**
	 * 该回调函数可能返回值,但返回值无法确定,
	 * 因此这里使用了泛型由调用者确定具体返回值的类型
	 * 
	 * @param param
	 * @return
	 */
	 V doCallback(P param);
	
}
