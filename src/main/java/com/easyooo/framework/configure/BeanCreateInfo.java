package com.easyooo.framework.configure;

import java.util.Map;
import java.util.Properties;

/**
 * 创建Bean所需要的参数
 *
 * @author Killer
 */
public class BeanCreateInfo {
	
	private Class<?> beanClass;
	private String beanId;
	
	private Properties properties;
	
	// 是定义bean的attribute，
	// 与XML的bean标签的属性一致
	private Map<String, Object> attributes;
	
	public BeanCreateInfo(Class<?> clazz,String beanId, Properties properties, Map<String, Object> attributes) {
		this.beanClass = clazz;
		this.beanId = beanId;
		this.properties = properties;
		this.attributes = attributes;
	}
	
	public Class<?> getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	public String getBeanId() {
		return beanId;
	}
	public void setBeanId(String beanId) {
		this.beanId = beanId;
	}
	public Properties getProperties() {
		return properties;
	}
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
}
