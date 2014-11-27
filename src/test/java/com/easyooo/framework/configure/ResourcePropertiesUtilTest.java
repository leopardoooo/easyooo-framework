package com.easyooo.framework.configure;

import java.net.URL;

import org.junit.Test;
import org.springframework.core.SpringProperties;

/**
 *
 * @author Killer
 */
public class ResourcePropertiesUtilTest {
	
	@Test
	public void getInputStream(){
		
		ClassLoader cl = SpringProperties.class.getClassLoader();
		URL url = cl.getResource("defaults/jdbc.properties");
		if (url != null) {
			System.out.println("OK.");
		}else{
			System.out.println("failure.");
		}
	}

}
