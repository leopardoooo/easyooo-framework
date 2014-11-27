package com.easyooo.framework.configure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.util.StringUtils;

/**
 * 根据配置创建Spring 能识别的<code>BeanDefineition对象</code>
 * 
 * 创建的Bean对象，不支持有依赖的Bean
 *
 * @author Killer
 */
public abstract class BeanDefinitionFactory {
	
	static final String ID = "id";

	/**
	 *  根据Bean信息创建 Spring BeanDefinition
	 *  
	 *  参数的BeanClass是必须设置的，其次ID等属性是可选的
	 *  Properties也是可选择
	 *  
	 * @param beanInfo
	 * @return
	 */
	public static BeanDefinitionHolder createBeanDefinition(BeanCreateInfo beanInfo){
		if(beanInfo == null || beanInfo.getBeanClass() == null){
			return null;
		}
		
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.
				rootBeanDefinition(beanInfo.getBeanClass());
		
		BeanDefinition definition = builder.getBeanDefinition();
		
		if(!StringUtils.isEmpty(beanInfo.getBeanId())){
			definition.setAttribute(ID, beanInfo.getBeanId()); 
		}
		
		if(beanInfo.getAttributes() != null){
			Map<String, Object> attrs = beanInfo.getAttributes();
			
			Set<String> keySet = attrs.keySet();
			for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
				String key = iterator.next();
				definition.setAttribute(key, attrs.get(key));
			}
		}
		
		if(beanInfo.getProperties() != null){
			Properties props = beanInfo.getProperties();
			Set<Object> keySet = props.keySet();
			for (Iterator<?> iterator = keySet.iterator(); iterator.hasNext();) {
				Object key = iterator.next();
				builder.addPropertyValue(key.toString(), props.get(key));
			}
		}
        
		return new BeanDefinitionHolder(builder.getBeanDefinition(),
				beanInfo.getBeanId());
	}
	
	/**
	 * 批量创建BeanDefinition
	 * @param multiBean
	 * @return
	 */
	public static List<BeanDefinitionHolder> createMultiBeanDefinition(List<BeanCreateInfo> multiBean){
		if(multiBean == null){
			return null;
		}
			
		List<BeanDefinitionHolder> multiTarget = new ArrayList<>();
		for (BeanCreateInfo create : multiBean) {
			BeanDefinitionHolder bd = createBeanDefinition(create);
			if(bd != null){
				multiTarget.add(bd);
			}
		}
		
		return multiTarget;
	}
	
	
}
