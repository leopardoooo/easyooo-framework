package com.easyooo.framework.configure;

import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.util.StringUtils;

import com.easyooo.framework.common.util.CollectionUtil;
import com.easyooo.framework.configure.BeanCreateInfo;
import com.easyooo.framework.configure.BeanDefinitionFactory;
import com.easyooo.framework.configure.NodeExecutor;
import com.easyooo.framework.configure.OverrideProperties;
import com.easyooo.framework.configure.ResourcePropertiesUtil;
import com.easyooo.framework.configure.UnifiedConfigException;
import com.easyooo.framework.support.zookeeper.ZookeeperExpcetion;
import com.easyooo.framework.support.zookeeper.ZookeeperTemplate;

/**
 * 这是一个工厂类构造器，支持标准的单节点、属性配置文件的加载方式，返回一个标准的Spring Bean Defintion
 * 
 * @author Killer
 */
public class FactoryBeanNodeExecutor implements NodeExecutor, InitializingBean {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	/** 
	 * 该参数设置之后
	 * 意味着classpath下面有个名为该参数的properties文件
	 */
	protected String defaultPropertiesShortName;

	/** 完整的节点名称  */
	protected String znode;

	/** 工厂类的BeanId */
	protected String factoryBeanId;

	/** 工厂类Class */
	protected Class<?> factoryClass;

	/** 必须的属性名称 */
	protected List<String> requiredProerites;

	
	/**
	 * 解析Node完整流程
	 * 
	 * @param zookeeperTemplate
	 * @param source
	 * @return
	 * @throws UnifiedConfigException
	 * @throws IOException
	 * @throws ZookeeperExpcetion
	 */
	public List<BeanDefinitionHolder> doExecuteInBox(
			ZookeeperTemplate zookeeperTemplate, String source)
			throws UnifiedConfigException, IOException, ZookeeperExpcetion {

		// default properties
		OverrideProperties defaultProperties = getDefaultProperties(defaultPropertiesShortName);

		// read zk
		byte[] data = zookeeperTemplate.readData(znode);
		String propertiesString = new String(data);
		Properties p = defaultProperties.overrideProperties(new StringReader(
				propertiesString));
		checkProperties(p);

		// create Bean
		return CollectionUtil.gList(createFactoryBeanDefinition(p));
	}
	
	/**
	 * 加载默认配置文件
	 * 
	 * @see ResourcePropertiesUtil#getPropertiesInputStream(String)
	 * @param shortName
	 *            属性文件的名称
	 * @return
	 * @throws UnifiedConfigException
	 */
	protected OverrideProperties getDefaultProperties(String shortName)
			throws IOException {
		InputStream inputStream = ResourcePropertiesUtil
					.getPropertiesInputStream(shortName);
		OverrideProperties defaultProperties = new OverrideProperties();
		if (inputStream == null) {
			logger.warn(String .format("The default %s.properties in the classpath does not exist", shortName));
		} else {
			defaultProperties.loadDefaultProperties(inputStream);
			inputStream.close();
		}
		return defaultProperties;
	}
	
	protected void checkProperties(Properties p) throws UnifiedConfigException {
		if (requiredProerites != null && requiredProerites.size() > 0) {
			for (String propertyName : requiredProerites) {
				if (StringUtils.isEmpty(p.getProperty(propertyName))) {
					throw new UnifiedConfigException(String.format(
							"property '%s' is a required in the %s znode.",
							propertyName, defaultPropertiesShortName));
				}
			}
		}
	}

	protected BeanDefinitionHolder createFactoryBeanDefinition(
			Properties properties) throws UnifiedConfigException {
		BeanCreateInfo beanInfo = new BeanCreateInfo(factoryClass,
				factoryBeanId, properties, null);
		return BeanDefinitionFactory.createBeanDefinition(beanInfo);
	}

	@Override
	public List<BeanDefinitionHolder> doExecute(
			ZookeeperTemplate zookeeperTemplate, String source)
			throws UnifiedConfigException {
		try {
			return doExecuteInBox(zookeeperTemplate, source);
		} catch (IOException e) {
			throw new UnifiedConfigException(e);
		} catch (ZookeeperExpcetion e) {
			throw new UnifiedConfigException(e);
		} catch (UnifiedConfigException uce) {
			throw uce;
		}
	}

	public void setDefaultPropertiesShortName(String defaultPropertiesShortName) {
		this.defaultPropertiesShortName = defaultPropertiesShortName;
	}

	public void setZnode(String znode) {
		this.znode = znode;
	}

	public void setFactoryBeanId(String factoryBeanId) {
		this.factoryBeanId = factoryBeanId;
	}

	public void setFactoryClass(Class<?> factoryClass) {
		this.factoryClass = factoryClass;
	}

	public void setRequiredProerites(List<String> requiredProerites) {
		this.requiredProerites = requiredProerites;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		notNull(defaultPropertiesShortName, "Property 'defaultPropertiesShortName' is required");
		notNull(znode, "Property 'znode' is required");
		notNull(factoryBeanId, "Property 'factoryBeanId' is required");
		notNull(factoryClass, "Property 'factoryClass' is required");
		notEmpty(requiredProerites, "requiredProerites must not be empty: it must contain at least 1 element");
	}

}
