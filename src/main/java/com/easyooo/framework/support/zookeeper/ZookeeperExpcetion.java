package com.easyooo.framework.support.zookeeper;


/**
 *
 * @author Killer
 */
@SuppressWarnings("serial")
public class ZookeeperExpcetion extends Throwable{

	public ZookeeperExpcetion(String msg, Throwable e) {
		super(msg, e);
	}
	
	public ZookeeperExpcetion(Throwable e) {
		super(e);
	}
}
