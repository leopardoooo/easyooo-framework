package com.easyooo.framework.common.net;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 *
 * 进程工具类
 *
 * @author Killer
 */
public class ProcessUtil {
	
	private static final Integer PID;
	
	static {
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();  
		// format: "pid@hostname"  
        String name = runtime.getName(); 
        String pidString = name.substring(0, name.indexOf('@'));
        PID = Integer.parseInt(pidString);  
	}
	
	/**
	 * 可获取当前进程号
	 * @return
	 */
	public static Integer getCurrentPid(){
		return PID;
	}
	
}
