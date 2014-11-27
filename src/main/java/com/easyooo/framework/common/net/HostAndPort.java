package com.easyooo.framework.common.net;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 *
 * Host Port 解析类
 * 支持如：
 * 192.168.1.204:8080
 * 192.168.1.204:9001,192.168.1.204:9002
 * 
 * @author Killer
 */
public class HostAndPort {

	/** 默认的web端口 
	private static final Integer DEFAULT_WEB_PORT = 80;
	*/
	
	/** 主机名或IP地址 */
	private String host;
	
	/** 端口号选值[0..65535]区间 */
	private Integer port;
	
	public HostAndPort(String host, Integer port){
		this.host = host;
		this.port = port;
	}
	
	public HostAndPort(String host){
		this.host = host;
	}
	
	public static HostAndPort fromString(String hostAndPort){
		List<HostAndPort> hapList = fromStringArray(hostAndPort);
		if(hapList.size() >= 1){
			return hapList.get(0);
		}
		return null;
	}
	
	/**
	 * 转换固定格式的ip和port字符串，
	 * 如192.168.1.1:8080，支持多个host和port,
	 * 多个使用','符号隔开，
	 * 如：192.168.1.1:9001,192.168.1.1:9002
	 * 
	 * @param hostAndPort 
	 * @return
	 */
	public static List<HostAndPort> fromStringArray(String hostAndPorts){
		if(StringUtils.isEmpty(hostAndPorts)){
			return null;
		}
		String[] hps = hostAndPorts.split(",");
		
		List<HostAndPort> hpList = new ArrayList<>();
		for (String hp : hps) {
			String[] str = hp.split(":");
			HostAndPort hap = null;
			if(str.length == 1){
				hap = new HostAndPort(str[0]);
			}else if(str.length == 2){
				hap = new HostAndPort(str[0], Integer.parseInt(str[1]));
			}else{
				throw new IllegalArgumentException("'" + hostAndPorts + "' format error");
			}
			hpList.add(hap);
		}
		
		return hpList;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}
	
	
	
}
