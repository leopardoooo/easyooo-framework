package com.easyooo.framework.cache.transaction;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * RocketMQ 消息消费者配置工厂Bean
 * 
 * @author Killer
 */
public class ConsumerFactoryBean extends DefaultMQPushConsumer implements InitializingBean,
		FactoryBean<DefaultMQPushConsumer> {

	private String applicationName;
	
	private String topicName = ClusterConstant.DEFAULT_TOPIC_NAME;

	@Override
	public void afterPropertiesSet() throws Exception {
		setConsumerGroup(applicationName);
		setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
		setConsumeMessageBatchMaxSize(1);
		
		setMessageModel(MessageModel.BROADCASTING);
		
		subscribe(topicName, "*");
		setMessageListener(new JvmCacheMessageListenerOrderly());
		
		start();
	}

	@Override
	public DefaultMQPushConsumer getObject() throws Exception {
		return this;
	}

	@Override
	public Class<?> getObjectType() {
		return DefaultMQPushConsumer.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
}
