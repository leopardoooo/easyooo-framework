package com.easyooo.framework.common.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Class Utils
 * 
 * @author Killer
 */
public class ClassUtil {

	public static Class<?> getGenericReturnType(Method method, int index) {
		if(method == null){
			return null;
		}
		Type returnType = method.getGenericReturnType();
		if (returnType instanceof ParameterizedType){
			Type[] types = ((ParameterizedType) returnType)
					.getActualTypeArguments();
			return (Class<?>)types[index];
		}
		
		return null;
	}

	public static boolean hasInterface(Class<?> sclass, Class<?> tclass) {
		do {
			Class<?>[] interfaces = sclass.getInterfaces();
			for (Class<?> clazz : interfaces) {
				if (clazz == tclass)
					return true;
			}
			sclass = sclass.getSuperclass();
		} while (sclass != null);
		return false;
	}

	public static boolean isPrimitive(Object o) {
		if (o.getClass().isPrimitive() || o instanceof Byte
				|| o instanceof Character || o instanceof Short
				|| o instanceof Integer || o instanceof Float
				|| o instanceof Long || o instanceof Double) {
			return true;
		} else {
			return false;
		}
	}
}
