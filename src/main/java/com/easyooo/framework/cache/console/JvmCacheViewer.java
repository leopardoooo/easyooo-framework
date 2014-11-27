package com.easyooo.framework.cache.console;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.easyooo.framework.cache.storage.JVMCache;

/**
 * JVM CAche Viewer
 *
 * @author Killer
 */
public class JvmCacheViewer implements CacheViewer {

	private Map<String, Object> cache = new JVMCache().getCacheObject();

	@Override
	public String get(String cacheKey) {
		Object o = cache.get(cacheKey);
		if(o != null){
			return o.toString();
		}
		return null;
	}

	@Override
	public List<String> keys(String cacheKeyPattern) {
		List<String> keyList = new ArrayList<String>();
		
		if(cacheKeyPattern == null || "".equals(cacheKeyPattern)){
			return keyList;
		}
		
		Set<String> keySet = cache.keySet();
		Pattern regex = parseCacheKeyPattern(cacheKeyPattern);
		
		for (String key : keySet) {
			if(regex.matcher(key).matches()){
				keyList.add(key);
			}
		}
		// sort list
		Collections.sort(keyList);
		
		// return sorted list
		return keyList;
	}
	
	private Pattern parseCacheKeyPattern(String cacheKeyPattern){
		return Pattern.compile("^"+ cacheKeyPattern +"$");
	}
	
	@Override
	public String getDescription() {
		return "------- 监控本地JVM缓存 ---------";
	}
	
}
