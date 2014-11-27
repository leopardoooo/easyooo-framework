package com.easyooo.framework.cache;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.easyooo.framework.demo.domain.TUser;
import com.easyooo.framework.demo.mapper.TUserDetailMapper;
import com.easyooo.framework.demo.mapper.TUserMapper;
import com.easyooo.framework.support.mybatis.Pagination;

/**
 * cache & mapper 集成测试
 * 
 * @author Killer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class CacheMapperTest{
	
	@Autowired
	private TUserMapper tUserMapper;
	
	@Autowired
	private ApplicationContext app;
	
	@Autowired
	private TUserDetailMapper tUserDetailMapper;
	
	private static Long userId;
	
	@Before
	public void setUp(){
		userId = Math.round(new Random().nextDouble() * 1000);
		TUser user = new TUser(userId);
		user.setUserName("killer");
		user.setStatus("NO");
		tUserMapper.insert(user);
	}
	
	@After
	public void destory(){
		TUser user = new TUser(userId);
		tUserMapper.deleteByPrimaryKey(user);
	}
	
	@Test
	public void __0_select_ByKey(){
		TUser cond = new TUser(userId);
		TUser user = tUserMapper.selectByPrimaryKey(cond);
		
		System.out.println("结果：" + user);
		
		assertThat(user, hasProperty("status", is("NO")));
		assertThat(user, hasProperty("userName", is("killer")));
	}
	
	@Test
	@Ignore
	@Transactional
	public void __1_select_ByUserName(){
		TUser cond = new TUser();
		cond.setUserName("killer");
		
		List<TUser> user = tUserMapper.selectByUserName(cond);
		assertThat(user, hasSize(1));
		assertThat(user.get(0), hasProperty("status", is("NO")));
	}
	
	@Test
	@Ignore
	public void __2_selectByStatus(){
		TUser cond = new TUser();
		cond.setStatus("NO");
		List<TUser> user = tUserMapper.selectByStatus(cond);
		assertThat(user, hasSize(1));
		assertThat(user.get(0), hasProperty("userName", is("killer")));
	}
	
	@Test
	@Ignore
	public void __3_updateByPrimaryKey(){
		TUser cond = new TUser();
		cond.setUserId(userId);
		cond.setUserName("dlj");
		cond.setStatus(null);
		
		// 这里会输出一个错误异常，因为是全量修改但没有设置分组信息
		// 这在某些场景下也是可行的。
		// 只是一个错误输出，不影响后续执行
		// Unable to generate group cache key
		int rows = tUserMapper.updateByPrimaryKey(cond);
		assertThat(rows, is(1));
		
		TUser cond1 = new TUser();
		cond1.setUserId(userId);
		cond1.setStatus("NO");
		List<TUser> user1 = tUserMapper.selectByStatus(cond1);
		assertThat(user1, hasSize(0));
		
		TUser cond2 = new TUser();
		cond2.setUserId(userId);
		cond2.setUserName("dlj");
		List<TUser> user2 = tUserMapper.selectByUserName(cond2);
		assertThat(user2, hasSize(1));
	}
	
	@Test
	@Ignore
	public void __4_updateByPrimaryKeySelective(){
		TUser cond = new TUser();
		cond.setUserId(userId);
		cond.setStatus("YES");
		
		int rows = tUserMapper.updateByPrimaryKeySelective(cond);
		assertThat(rows, is(1));
		
		TUser cond1 = new TUser();
		cond1.setUserId(userId);
		cond1.setStatus("NO");
		List<TUser> user1 = tUserMapper.selectByStatus(cond1);
		assertThat(user1, hasSize(0));
		
		TUser cond2 = new TUser();
		cond2.setUserId(userId);
		cond2.setUserName("killer");
		List<TUser> user2 = tUserMapper.selectByUserName(cond2);
		assertThat(user2, hasSize(1));
	}
	
	@Test
	@Ignore
	public void __5_updateDate(){
		TUser cond = new TUser();
		cond.setUserId(userId);
		cond.setCreateDate1(new Date());
		cond.setCreateDate2(new Date());
		cond.setCreateDate3(new Date());
		
		int rows = tUserMapper.updateByPrimaryKeySelective(cond);
		assertThat(rows, is(1));
		
		TUser user = tUserMapper.selectByPrimaryKey(cond);
		assertThat(user, notNullValue());
	}
	
	@Test
	@Ignore
	public void __10_testSelectAllWithPage(){
		Pagination p = new Pagination(null, 0, 2);
		List<TUser> users = tUserMapper.selectAllWithPage(p);
		users = tUserMapper.selectAllWithPage(p);
		assertThat(users, hasSize(2));
	}
}
