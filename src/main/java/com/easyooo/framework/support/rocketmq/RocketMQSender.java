package com.easyooo.framework.support.rocketmq;

import static org.springframework.util.Assert.notNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.easyooo.framework.support.redis.RedisTemplate;
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
	
	static final String DEFAULT_RMQ_ERROR_QUEUE = "__rmqErrorQueue";
	
	private String rmqErrorQueueKey = DEFAULT_RMQ_ERROR_QUEUE;
	
	private RedisTemplate redisTemplate;
	
	/** 容错标记， 标记为true时，发送失败则发送至redis队列 */
	private boolean needFaultTolerant = false;
	
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
		try{
			SendResult sr = producer.send(data);
			if(sr.getSendStatus() != SendStatus.SEND_OK){
				logger.warn(String.format("Message(%s) has been sent successfully, but send status is %s.", 
						data.getTopic(), sr.getSendStatus()));
			}
		}catch(Exception e){
			if(needFaultTolerant){
				putInErrorQueue(data);
			}
			throw e;
		}
		return true;
	}
	
	/**
	 * 将消息发送至错误消息队列
	 */
	protected void putInErrorQueue(Message msg){
		String msgString = JSON.toJSONString(msg);
		redisTemplate.rpush(rmqErrorQueueKey, msgString);
		logger.warn("消息{}发送至RMQ失败，已rpush至{}缓存", msgString, rmqErrorQueueKey);
	}
		
	@Override
	public void afterPropertiesSet() throws Exception {
		notNull(producer, "Property 'producer' is required");
		
		if(needFaultTolerant){
			notNull(redisTemplate, "Property 'redisTemplate' is required");
		}
	}

	@Override
	public String getProviderInfo() {
		return PROVIDER_NAME;
	}

	public void setRmqErrorQueueKey(String rmqErrorQueueKey) {
		this.rmqErrorQueueKey = rmqErrorQueueKey;
	}

	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void setNeedFaultTolerant(boolean needFaultTolerant) {
		this.needFaultTolerant = needFaultTolerant;
	}
}
