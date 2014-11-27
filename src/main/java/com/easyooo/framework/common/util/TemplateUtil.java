package com.easyooo.framework.common.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 字符串模板工具类，
 * 
 * 模板支持如："hello, {user}"， 
 * 参数须以Key-Value形式的Map参数
 * 模板中可支持"."符号
 *
 * @author Killer
 */
public final class TemplateUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(TemplateUtil.class);
	
	private static final String TEMPLATE_REGEX = "(\\{\\s*[\\w\\.]+\\s*\\})";
	private static final Pattern PATTERN;
	static {
		PATTERN = Pattern.compile(TEMPLATE_REGEX);
	}
	
	public static String format(String template, Object...kvArgs){
		return format(template, MapUtil.gmap(kvArgs));
	}
	
	public static String format(String template, Map<String, Object> args){
		Matcher m = PATTERN.matcher(template);
		StringBuffer buffer = new StringBuffer();
		while(m.find()){
			String group = m.group();
			String argKey = group.substring(1, group.length() - 1).trim();
			Object v = args.get(argKey);
			if(v != null){
				m.appendReplacement(buffer, v.toString());
			}
			
			if(logger.isDebugEnabled()){
				logger.debug(argKey + ":" + v);
			}
		}
		m.appendTail( buffer);
		return buffer.toString();
	}
}
