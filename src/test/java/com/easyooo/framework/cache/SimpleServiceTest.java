package com.easyooo.framework.cache;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.easyooo.framework.demo.service.SimpleService;

/**
 * Mapper Test Case 
 *
 * @author Killer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class SimpleServiceTest {
	
	@Autowired
	private SimpleService simpleService;
	
	@Test
	public void testInsert(){
		long userId = 1002L;
		simpleService.insertUser(userId);
	}
	
	@Test
	@Ignore
	public void testDelete(){
		assertThat(simpleService.deleteUser(1000L), is(true));
	}

}
