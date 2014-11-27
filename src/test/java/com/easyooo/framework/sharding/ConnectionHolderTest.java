package com.easyooo.framework.sharding;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.easyooo.framework.sharding.transaction.ConnectionHolder;

/**
 *
 * @author Killer
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class ConnectionHolderTest {

	
	@Test
	public void testEqualUserAndPassword(){
		ConnectionHolder holder = new ConnectionHolder();
		assertThat(holder.equalUserAndPwd(null, null), is(true));
		assertThat(holder.equalUserAndPwd("", null), is(false));
		assertThat(holder.equalUserAndPwd("", ""), is(false));
		assertThat(holder.equalUserAndPwd(null, ""), is(false));
		
		holder.setUser("root");
		holder.setPassword("root");
		assertThat(holder.equalUserAndPwd("root", "root"), is(true));
		assertThat(holder.equalUserAndPwd(null, "root"), is(false));
		assertThat(holder.equalUserAndPwd("root", null), is(false));
		assertThat(holder.equalUserAndPwd("111", "aaa"), is(false));
		
	}
	
}
