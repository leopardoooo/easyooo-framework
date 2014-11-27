package com.easyooo.framework.configure;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Override Properties Simple Wrapper
 *
 * @author Killer
 * @since 1.0
 */
public class OverrideProperties {
	
	final Logger logger = LoggerFactory.getLogger(getClass());

	private boolean hasDefaultProperties = false;
	
	private Properties defaultProperties;
	
	/**
	 * 加载默认的属性，如果重复调用，会覆盖默认属性
	 * 
	 * @param stream 
	 */
	public void loadDefaultProperties(InputStream stream)throws IOException{
		defaultProperties = new Properties();
		defaultProperties.load(stream);
		
		this.hasDefaultProperties = true;
	}
	
	/**
	 * 
	 * 保证传入的Reader是一个标准的Properties语法
	 * 该方法不会覆盖 {@link #loadDefaultProperties(InputStream)} 加载的Properties
	 * 拷贝一个副本
	 * 
	 * @param reader 任何实现了Reader接口的读取器, 如：StringReader
	 */
	public Properties overrideProperties(Reader reader)throws IOException{
		Properties overrideProps = null;
		if(hasDefaultProperties){
			overrideProps = cloneDefaultProperties();
		}else{
			// no defaults
			overrideProps = new Properties();
		}
		overrideProps.load(reader);
		
		return overrideProps;
	}
	
	/**
	 * 读取默认属性，获得一个克隆的版本
	 * 
	 * @return Clone Default Properties
	 */
	public Properties getDefaultProperties(){
		return cloneDefaultProperties();
	}
	
	private Properties cloneDefaultProperties(){
		if(this.hasDefaultProperties){
			Properties tmp = new Properties();

			Enumeration<?> keys = defaultProperties.propertyNames();
			while(keys.hasMoreElements()){
				Object o = keys.nextElement();
				tmp.put( o , defaultProperties.get(o));
			}
			return tmp;
		}
		return null;
	}
}
