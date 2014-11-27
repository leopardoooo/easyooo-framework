package com.easyooo.framework.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanMap;
import net.sf.cglib.beans.BulkBean;
import net.sf.cglib.core.Converter;

import org.springframework.util.StringUtils;

/**
 * 简单包装了CGlib对Bean的反射操作
 *
 *
 * @author Killer
 */
public final class CglibUtil {
	
	/**
	 * 将from实体bean的属性值拷贝至to实体类的同名属性值， 可使用自定义转换器，
	 * 如果使用了转换器，则由转换器完成所有的属性拷贝
	 * 
	 * @param from 源对象
	 * @param to 目标对象
	 * @param converter
	 */
	public static void copy(Object from, Object to, Converter converter) {
		if (from == null || to == null) {
			return;
		}
		boolean useConverter = (converter != null ? true : false);
		BeanCopier copier = BeanCopier.create(from.getClass(), to.getClass(),
				useConverter);
		copier.copy(from, to, converter);
	}
	
	/**
	 * 将源对象属性拷贝至目标属性值，只有同名同类型的才会被拷贝
	 * @param from
	 * @param to
	 */
	public static void copy(Object from, Object to) {
		copy(from, to, null);
	}
	
	/**
	 * 将Bean属性值封装至Map
	 * @param bean
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map describe(Object bean){
		Map target = new HashMap();
		target.putAll(BeanMap.create(bean));
		return target;
	}
	
	/**
	 * 获取Bean中部分属性值
	 * @param bean
	 * @param propertyNames 属性名称
	 */
	public static <T> Object[] getPropertyValue(Object bean, String... propertyNames){
		if(bean == null || propertyNames.length == 0){
			return null;
		}
		Map<?,?> beanMap = describe(bean);
		Object[] obj = new Object[propertyNames.length];
		for (int i = 0; i < obj.length; i++) {
			obj[i] = beanMap.get(propertyNames[i]);
		}
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getPropertyValue(Object bean, String propertyName){
		if(StringUtils.isEmpty(propertyName)){
			return null;
		}
		Object[] obj = getPropertyValue(bean, new String[]{propertyName});
		if(obj == null || obj[0] == null){
			return null;
		}
		return (T)(obj[0]);
	}
	
	/**
	 * 反射设置多个属性值
	 * @see #genGetterMethodName(String)
	 * @see #genSetterMethodName(String)
	 */
	public static <T> void setPropertyValues(Object bean, String[] names, Object[] values){
		if(bean == null)
			return;
		if(names == null || names.length == 0)
			return;
		if(values == null)
			return;
		if(names.length != values.length){
			throw new IllegalArgumentException("names length is "
					+ names.length + ", but values length is " + values.length);
		}
		
		String[] getters = new String[names.length];
		String[] setters = new String[names.length];
		Class<?>[] types = new Class<?>[names.length];
		
		for (int i = 0; i < names.length; i++) {
			if(values[i] == null){
				continue;
			}
			String name = names[i];
			getters[i] = genGetterMethodName(name);
			setters[i] = genSetterMethodName(name);
			types[i] = values[i].getClass();
		}
		
		BulkBean bb = BulkBean.create(bean.getClass(), getters, setters, types);
		bb.setPropertyValues(bean, values);
	}
	
	/**
	 * 反射设置单个属性值
	 * @see #setPropertyValues(Object, String[], Object[])
	 */
	public static void setPropertyValue(Object bean, String propertyName, Object value){
		setPropertyValues(bean, new String[]{propertyName}, new Object[]{value});
	}
	
	/**
	 * 将map的值拷贝至对象中, 如果类型不一致将会导致转换异常
	 * @param from
	 * @param to
	 */
	@SuppressWarnings("rawtypes")
	public static void populate(Map from, Object to) 
			throws IllegalAccessException, IllegalArgumentException, 
			InvocationTargetException{
		if(from == null || from.size() == 0 || to == null){
			return ;
		}
		
		Method[] methods = to.getClass().getDeclaredMethods();
		for (Object obj: from.entrySet()) {
			Map.Entry entry = (Map.Entry)obj;
			Object value = entry.getValue();
			String propertyName = entry.getKey().toString();
			for (Method method : methods) {
				if(method.getName().equals(genSetterMethodName(propertyName))){
					method.invoke(to, value);
					break;
				}
			}
		}
	}
	
	/**
	 * 生成Getter方法名称，仅支持驼峰命名规则
	 */
	public static String genGetterMethodName(String propertyName){
		return "get" + StringUtils.capitalize(propertyName);
	}
	
	/**
	 * 生成Setter方法名称，仅支持驼峰命名规则
	 */
	public static String genSetterMethodName(String propertyName){
		return "set" + StringUtils.capitalize(propertyName);
	}
	
}
