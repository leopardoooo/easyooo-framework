package com.easyooo.framework.sharding;

/**
 * 如果需要水平分库，则表操作都应该传入实现了该类的接口方法的实体类进行操作
 * 
 * 如：delete(ShardingStrategy t)
 *
 * @author Killer
 */
public interface ShardingStrategy {
	
	
	/**
	 * 获取水平切分的关键字段的值
	 * 
	 * @return
	 */
	Long getShardingKeyword();

}
