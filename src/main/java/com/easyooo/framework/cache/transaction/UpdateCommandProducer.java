package com.easyooo.framework.cache.transaction;

import static org.springframework.util.Assert.notNull;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.common.message.Message;
import com.easyooo.framework.cache.seriaziler.JsonSeriaziler;

/**
 * 更新消息生产者，通过 MQ 将JVM所有的消息同步到指定的队列
 * 
 * @see BufferedCacheRemoteSender
 *
 * @author Killer
 */
public class UpdateCommandProducer implements InitializingBean{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private JsonSeriaziler seriaziler = new JsonSeriaziler();
	
	/**
	 * 如果没有自动注入，则采用手工注入的方式
	 */
	@Autowired(required = false)
	private DefaultMQProducer mqProducer;
	
	/***
	 * 提交命令，一般在事务提交之后，提交所有的请求操作 
	 * 将对象序列化成JSON字符串发送
	 * 
	 * @param commands
	 */
	public void submitUpdate(Collection<Command> commands)throws JvmUpdateException{
		if(commands == null || commands.size() == 0){
			return ;
		}
		try{
			UpdateBody body = createUpdateBody(commands);
			String bodyString = seriaziler.seriazileAsString(body);
			Message msg = new Message(ClusterConstant.DEFAULT_TOPIC_NAME, bodyString.getBytes());
			mqProducer.send(msg);
			logger.info("Jvm Cache Update Message is sent successfully.");
		}catch(Exception e){
			throw new JvmUpdateException(e);
		}
	}
	
	private UpdateBody createUpdateBody(Collection<Command> commands){
		return new UpdateBody(ClientIdGenerator.getClientId(), commands);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		notNull(mqProducer, "Property 'mqProducer' is required");
	}

	public void setMqProducer(DefaultMQProducer producer) {
		this.mqProducer = producer;
	}
}
