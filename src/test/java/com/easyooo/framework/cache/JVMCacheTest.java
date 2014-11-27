package com.easyooo.framework.cache;

import org.junit.Test;

import com.easyooo.framework.cache.storage.JVMCache;

/**
 *
 * @author Killer
 */
public class JVMCacheTest {
	
	@Test
	public void testStaticJvm()throws CacheException{
		JVMCache cache1 = new JVMCache();
		JVMCache cache2 = new JVMCache();
		
		boolean bool = cache1.set("a", "1");
		System.out.println(bool);
		String res = cache2.get("a");
		System.out.println(res);
	}
}
