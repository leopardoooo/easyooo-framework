package com.easyooo.framework.configure;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinitionHolder;

import com.easyooo.framework.support.zookeeper.ZookeeperTemplate;

/**
 * Node 执行器接口定义
 *
 * @author Killer
 */
public interface NodeExecutor {

	
	/**
	 * 当在直接子节点时就执行该方法
	 * 
	 * @param source
	 * @return
	 */
	List<BeanDefinitionHolder> doExecute(ZookeeperTemplate zookeeperTemplate,
			String source)throws UnifiedConfigException;

}
