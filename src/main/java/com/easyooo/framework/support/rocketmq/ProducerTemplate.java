package com.easyooo.framework.support.rocketmq;

import static org.springframework.util.Assert.notNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import com.alibaba.rocketmq.common.message.Message;
import com.easyooo.framework.support.transaction.RemoteSenderOrderly;

/**
 *
 * JMS Template for RocketMQ，
 * 
 * 如果在创建该实例时，没有明确的注入发送器，则会默认构造一个直接发送器注入到{@link #sender}
 * @author Killer
 */
public class ProducerTemplate implements InitializingBean{
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	/** 发送器接口注入  */
	private RemoteSenderOrderly<Message> sender;
	
	/**
	 * 发送简单消息
	 * 
	 * @param topic 队列
	 * @param body 消息体
	 * @return
	 * @throws Throwable
	 */
	public boolean send(String topic, String body) throws Throwable{
		return send(topic, null, null, body);
	}
	
	/**
	 * 发送消息
	 * @param topic 对列名
	 * @param tag 只支持设置一个Tag（服务端消息过滤使用）
	 * @param key 关键字查询使用
	 * @param body 消息体
	 * @return
	 * @throws Throwable
	 */
	public boolean send(String topic, String tag, String key, String body) throws Throwable{
		// topic is required
		if(StringUtils.isEmpty(topic)){
			return false;
		}
		// body is required
		if(StringUtils.isEmpty(body)){
			return false;
		}
		
		byte[] byteData = body.getBytes();
		Message msg = new Message(topic, tag, key, byteData);
		return sender.send(msg);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		notNull(sender, "Property 'sender' is required");
	}

	public void setSender(RemoteSenderOrderly<Message> sender) {
		this.sender = sender;
	}
}
