package com.easyooo.framework.support.rocketmq;

import static org.springframework.util.Assert.notNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.easyooo.framework.support.transaction.RemoteSenderOrderly;

/**
 * JMS消息发送器，依赖 DefaultMQProducer
 *
 * @author Killer
 */
public class RocketMQSender implements RemoteSenderOrderly<Message>, InitializingBean{
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String PROVIDER_NAME = "ROCKETMQ";
	
	@Autowired(required = true)
	private DefaultMQProducer producer;
	
	public RocketMQSender(){
	}
	
	public RocketMQSender(DefaultMQProducer producer){
		this.producer = producer;
	}
	
	@Override
	public boolean send(Message data) throws Exception {
		if(data == null){
			return false;
		}
		SendResult sr = producer.send(data);
		if(sr.getSendStatus() != SendStatus.SEND_OK){
			logger.warn(String.format("Message(%s) has been sent successfully, but send status is %s.", 
					data.getTopic(), sr.getSendStatus()));
		}
		return true;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		notNull(producer, "Property 'producer' is required");
	}

	@Override
	public String getProviderInfo() {
		return PROVIDER_NAME;
	}
}
