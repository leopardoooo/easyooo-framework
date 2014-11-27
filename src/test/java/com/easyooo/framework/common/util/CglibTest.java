package com.easyooo.framework.common.util;

import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.core.Converter;

import org.junit.Ignore;
import org.junit.Test;

import com.easyooo.framework.common.util.CglibUtil;

/**
 *
 * @author Killer
 */
public class CglibTest {
	
	@Test
	@Ignore
	public void copy(){
		TestObject from = new TestObject();
		from.setId("1");
		from.setName("killer");
		TestObject to = new TestObject();
		CglibUtil.copy(from, to);
		System.out.println(to);
	}

	@Test
	@Ignore
	public void copyUseConvert(){
		TestObject from = new TestObject();
		from.setId("1");
		from.setName("killer");
		final TestObject2 to = new TestObject2();
		CglibUtil.copy(from, to, new Converter() {
			@SuppressWarnings("rawtypes")
			@Override
			public Object convert(Object value, Class target, Object context) {
				if(context.equals("setId")){
					return Integer.parseInt(value.toString());
				}
				return value;
			}
		});
		System.out.println(to);
	}
	
	@Test
	@Ignore
	public void describe(){
		TestObject2 from = new TestObject2();
		from.setId(1);
		from.setName("killer");
		
		Map<?,?> map = CglibUtil.describe(from);	
		System.out.println(map.keySet());
	}
	
	@Test
	@Ignore
	public void getPropertyValue(){
		TestObject to = new TestObject();
		to.setId("1");
		to.setName("killer");
		System.out.println(CglibUtil.getPropertyValue(to, "id"));
		System.out.println(CglibUtil.getPropertyValue(to, "id", "name"));
	}
	
	@Test
	@Ignore
	public void testPopulate()throws Exception{
		Map<Object,Object> from = new HashMap<Object, Object>();
		from.put("name", "killer");
		from.put("id", "1");
		long start = System.currentTimeMillis();
		for (int i = 0; i< 10000; i++) {
			TestObject to = new TestObject();
			CglibUtil.populate(from, to);
			System.out.println(to);
		}
		long end = System.currentTimeMillis();
		System.out.println((end - start) + "ms");
	}
	
	@Test
	public void copy2(){
		TestObject from = new TestObject();
		from.setId(null);
		from.setName("killer0");
		TestObject to = new TestObject();
		to.setId("1");
		to.setName("killer1");
		
		CglibUtil.copy(from, to);
		System.out.println(to);
	}

}
