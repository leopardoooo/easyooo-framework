package com.easyooo.framework.common.net;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import com.easyooo.framework.common.net.HostAndPort;

/**
 *
 * @author Killer
 */
public class HostAndPortTest {

	@Test
	public void testFormat(){
		HostAndPort hap = HostAndPort.fromString("192.168.1.203:9001");
		assertThat(hap, hasProperty("host", is("192.168.1.203")));
		assertThat(hap, hasProperty("port", is(9001)));
	}
	
	@Test
	public void testFormats(){
		List<HostAndPort> haps = HostAndPort.fromStringArray("192.168.1.203:9001,192.168.1.203:9002");
		assertThat(haps, hasSize(2));
		assertThat(haps.get(0), hasProperty("host", is("192.168.1.203")));
		assertThat(haps.get(0), hasProperty("port", is(9001)));
		
		assertThat(haps.get(1), hasProperty("host", is("192.168.1.203")));
		assertThat(haps.get(1), hasProperty("port", is(9002)));
	}
}
