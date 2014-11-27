package com.easyooo.framework.cache.seriaziler;

import org.junit.Test;

/**
 *
 * @author Killer
 */
public class JsonSeriazilerTest {
	
	@Test
	public void seriazileAsString() throws SerializationException {
		long start = System.currentTimeMillis();
		for(int i = 0; i< 10000; i++){
			SimpleObject to = new SimpleObject();
			to.setId("1");
			new JsonSeriaziler().seriazileAsString(to);
		}
		long end = System.currentTimeMillis();
		System.out.println("ms: " + (end - start));
	}

}
