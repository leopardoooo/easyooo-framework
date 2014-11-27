package com.easyooo.framework.cache.transaction;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.easyooo.framework.cache.seriaziler.JsonSeriaziler;
import com.easyooo.framework.cache.seriaziler.SerializationException;

/**
 * 在集群模式缓存下，监听来自其它JVM客户端对缓存做出的修改
 *
 * @author Killer
 */
public class JvmCacheMessageListenerOrderly implements MessageListenerOrderly{
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private JsonSeriaziler seriaziler = new JsonSeriaziler();
	
	private UpdateJvmCacheManager updateManager = new UpdateJvmCacheManager();
	
	@Override
	public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs,
			ConsumeOrderlyContext context) {
		MessageExt msg = msgs.get(0);
		String jsonString = new String(msg.getBody());
		try {
			UpdateBody body = seriaziler.deserializeAsObject(
					jsonString, UpdateBody.class);
			if(body == null){
				return ConsumeOrderlyStatus.SUCCESS;
			}
			
			logger.info("receive a synchronous JVM caching from "
					+ body.getClientId());
			
			if(ClientIdGenerator.getClientId().equals(body.getClientId())){
				logger.info("Ignore it, because he was himself a sender");
			}
			updateManager.update(body.getCommands());
		} catch (SerializationException e) {
			logger.error("serialized message error", e);
			if(!logger.isDebugEnabled()){
				logger.debug("message data: " + jsonString);
			}
		}
		
		return ConsumeOrderlyStatus.SUCCESS;
	}

}
