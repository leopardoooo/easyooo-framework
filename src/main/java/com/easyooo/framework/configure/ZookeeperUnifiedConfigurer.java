package com.easyooo.framework.configure;

import static org.springframework.util.Assert.notNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import com.easyooo.framework.support.zookeeper.ZookeeperExpcetion;
import com.easyooo.framework.support.zookeeper.ZookeeperTemplate;

/**
 * <p>这是一个统一配置的扫描器，扩展Spring BeanDefinition后置处理器</p>
 * <p>实现从远程服务器<code><i>zookeeper</i></code>读取统一配置信息并注入到Spring容器中</p>
 * <p>每个直接子节点都会触发执行器执行，由节点处理器负责完成配置的过程 </p>
 * 
 * @author Killer
 */
public class ZookeeperUnifiedConfigurer implements
		BeanDefinitionRegistryPostProcessor, InitializingBean{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String CONFIGURATION_PATH = "/configuration";
	
	private String configPath = CONFIGURATION_PATH;
	
	private Map<String, NodeExecutor> executorMap;
	
	private ZookeeperTemplate zookeeperTemplate;
	
	/**
	 * 如果设置了该属性，则不从远程读取，只从本地属性读取配置信息
	 * 意味着本地必须配置<code>connectionString</code>
	 */
	@SuppressWarnings("unused")
	private boolean debugMode = false;
	
	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// do nothing
		// end
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		 notNull(this.zookeeperTemplate, "Property 'zookeeperTemplate' is required");
		 notNull(this.executorMap, "ExecutorMap requires at least one element.");
	}

	@Override
	public void postProcessBeanDefinitionRegistry(
			BeanDefinitionRegistry registry) throws BeansException {
		List<String> nodes = null;
		try {
			nodes = getChildren(this.configPath);
			List<BeanDefinitionHolder> bdHolder = new ArrayList<>();
			for (String node : nodes) {
				List<BeanDefinitionHolder> tmp = publishNode(node); 
				if(tmp != null){
					bdHolder.addAll(tmp);
				}
			}
			
			for (BeanDefinitionHolder definitionHolder : bdHolder) {
				registerBeanDefinition(definitionHolder, registry);
			}
			
			if(logger.isDebugEnabled()){
				StringBuffer sb = new StringBuffer("[");
				int counter = 1;
				for (BeanDefinitionHolder dh : bdHolder) {
					sb.append(dh.getBeanName());
					
					if(counter ++  != bdHolder.size()){
						sb.append(",");
					}
				}
				
				sb.append("] registered into spring's container.");
				
				logger.debug(sb.toString());
			}
			
		} catch (UnifiedConfigException e) {
			throw new BeanCreationException("Failed to create bean", e);
		}
	}
	
	private List<String> getChildren(String configPath)throws UnifiedConfigException{
		try {
			return zookeeperTemplate.getChildren(configPath);
		} catch (ZookeeperExpcetion e) {
			throw new UnifiedConfigException("Gets the child nodes of the "
					+ configPath +" path to make mistakes", e);
		}
	}
	
	protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
		BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
	}
	
	public List<BeanDefinitionHolder> publishNode(String node)throws UnifiedConfigException{
		NodeExecutor executor = this.executorMap.get(node);
		
		if(executor == null){
			logger.warn("Node[ " + node + "] is discarded!");
			return null;
		}
		
		List<BeanDefinitionHolder> bdh = executor.doExecute(zookeeperTemplate, node);
		if(bdh != null && bdh.size() > 0){
			return bdh;
		}
		return null;
	}

	public void setZookeeperTemplate(ZookeeperTemplate zookeeperTemplate) {
		this.zookeeperTemplate = zookeeperTemplate;
	}

	public void setExecutorMap(Map<String, NodeExecutor> executorMap) {
		this.executorMap = executorMap;
	}

	public void setDebugMode(boolean debugMode) {
		throw new UnsupportedOperationException("Temporary does not support the debug mode");
	}
}
