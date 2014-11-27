package com.easyooo.framework.configure;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;

import org.springframework.util.StringUtils;

/**
 * 默认的 Properties资源类，加载ClassPath根目录下的文件，返回InputStream
 *
 * @author Killer
 */
public abstract class ResourcePropertiesUtil {
	static final String TEMPLATE_PROPERTIES = "defaults/{0}.properties";
	
	public static final String SHORT_PROPERTIES_JDBC = "jdbc";
	public static final String SHORT_PROPERTIES_JMS = "rockmq";
	public static final String SHORT_PROPERTIES_ZMQ = "zmq";
	public static final String SHORT_PROPERTIES_REDIS = "redis";
	
	public static InputStream getJdbcPropertiesInputStream(){
		return getPropertiesInputStream(SHORT_PROPERTIES_JDBC);
	}
	
	public static InputStream getJmsPropertiesInputStream(){
		return getPropertiesInputStream(SHORT_PROPERTIES_JMS);
	}
	
	public static InputStream getZmqPropertiesInputStream(){
		return getPropertiesInputStream(SHORT_PROPERTIES_ZMQ);
	}
	
	public static InputStream getRedisPropertiesInputStream(){
		return getPropertiesInputStream(SHORT_PROPERTIES_REDIS);
	}
	
	public static InputStream getPropertiesInputStream(String shortName){
		
		if(StringUtils.isEmpty(shortName)){
			return null;
		}
		
		String propFileName = MessageFormat.format(TEMPLATE_PROPERTIES,
				shortName);

		return getInputStream(propFileName);
	}
	
	public static InputStream getInputStream(String classPathFileName){
		if(null == classPathFileName){
			return null;
		}
		
		try {
			ClassLoader cl = ResourcePropertiesUtil.class.getClassLoader();
			URL url = cl.getResource(classPathFileName);
			if(url != null){
				return url.openStream();
			}
			return null;
		}catch (IOException ex) {
			return null;
		}
	}
}
