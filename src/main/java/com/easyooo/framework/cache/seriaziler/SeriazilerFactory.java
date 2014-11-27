package com.easyooo.framework.cache.seriaziler;

import java.lang.reflect.Type;

/**
 * 序列换工厂类
 *
 * @author Killer
 */
public class SeriazilerFactory implements Seriaziler{
	
	private Seriaziler seriaObj;
	
	public SeriazilerFactory(Seriaziler seri){
		this.seriaObj = seri;
	}

	@Override
	public String seriazileAsString(Object object)
			throws SerializationException {
		return seriaObj.seriazileAsString(object);
	}

	@Override
	public <T> T deserializeAsObject(String stringValue, Type type)
			throws SerializationException {
		return seriaObj.deserializeAsObject(stringValue, type);
	}
	
}
