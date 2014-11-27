package com.easyooo.framework.support.rocketmq;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;

/**
 * RocketMQ 消息生产者工厂Bean， 该类支持配置所有的参数，
 * 仅修改了producerGroup属性对应的配置项为 applicationName
 * 
 * @author Killer
 */
public class ProducerFactoryBean extends DefaultMQProducer implements InitializingBean, DisposableBean,
		FactoryBean<DefaultMQProducer> {

	private String applicationName;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		setProducerGroup(applicationName);
		start();
	}

	@Override
	public DefaultMQProducer getObject() throws Exception {
		return this;
	}

	@Override
	public Class<?> getObjectType() {
		return DefaultMQProducer.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void destroy() throws Exception {
		shutdown();
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
}
