package com.easyooo.framework.cache.seriaziler;

import java.lang.reflect.Type;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

/**
 * Json 字符串 与Object bean之间的序列化实现类
 * 
 * @see com.alibaba.fastjson.JSON
 * 
 * @author Killer
 */
public class JsonSeriaziler implements Seriaziler {
	
	private SerializeConfig mapping = new SerializeConfig();
	
	private SerializerFeature[] features = new SerializerFeature[]{
		SerializerFeature.SkipTransientField,
		SerializerFeature.WriteMapNullValue
	};
	
	// private PropertyFilter defaultFilter = new DefaultPropertyFilter();
	
	public JsonSeriaziler(){
	}
	
	public JsonSeriaziler(String dateFormat){
		features[features.length] = SerializerFeature.WriteDateUseDateFormat;
		mapping.put(java.util.Date.class, new SimpleDateFormatSerializer(dateFormat));
	}
	
	public JsonSeriaziler(SerializerFeature ... features){
		this();
		this.features = features;
	}

	/**
	 * java-object as json-string
	 * 
	 * @param object
	 * @return
	 */
	public String seriazileAsString(Object object)throws SerializationException {
		if (object == null) {
			return "";
		}
		try {
			return JSON.toJSONString(object, mapping, features);
		} catch (Exception ex) {
			throw new SerializationException("Could not write JSON: "
					+ ex.getMessage(), ex);
		}
	}

	/**
	 * json-string to java-object
	 * 
	 * @param str
	 * @return
	 */
	public <T> T deserializeAsObject(String jsonString, Type type)throws SerializationException {
		if (jsonString == null || type == null) {
			return null;
		}
		try {
			return JSON.parseObject(jsonString, type);
		} catch (Exception ex) {
			throw new SerializationException("Could not write JSON: "
					+ ex.getMessage(), ex);
		}
	}
	
	public static class DefaultPropertyFilter implements PropertyFilter{
		@Override
		public boolean apply(Object object, String name, Object value) {
			try {
				boolean exist = object.getClass().getDeclaredField(name) != null;
				return exist;
			} catch (Exception e) {
				return false;
			}
		}
	}
}
