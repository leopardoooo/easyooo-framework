package com.easyooo.framework.support.zookeeper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;

/**
 *
 * @author Killer
 */
public class InterProcessMutexTest {
	final static String zkConnectionString = "localhost:2181";
	
	private CuratorFramework zkClient;
	private String lockPath = "/my/curator";
	
	private int counter = 0;
	private final int THREAD_COUNT = 30;
	
	
	@Test
	public void testIfPrint() throws InterruptedException{
		final CountDownLatch gate = new CountDownLatch(1);
		final CountDownLatch endGate = new CountDownLatch(THREAD_COUNT);
		final List<String> bufferList = new ArrayList<String>();
		try{
			RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
			zkClient = CuratorFrameworkFactory.newClient(
					zkConnectionString, retryPolicy);
			zkClient.start();
			
			for (int i = 0; i < THREAD_COUNT; i++) {
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							gate.await();
							boolean flag = false;
							int value = 0;
							InterProcessMutex lock = new InterProcessMutex(zkClient, lockPath);
							if (lock.acquire(120, TimeUnit.SECONDS)){
							    try {
							    	counter ++; 
									if(counter % 2 == 0){
										flag = true;
										value = counter;
									}
							    }finally {
							        lock.release();
							    }
							}
							if(flag){
								bufferList.add(Thread.currentThread().getName() + " -> " + value);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							endGate.countDown();
						}
					}
				}).start();
			}
			System.out.println("开始计算...");
			gate.countDown();
			endGate.await();
			
			for (String s : bufferList) {
				System.out.println(s);
			}
			
			System.out.println("算法结束.");
			System.out.println("释放链接.");
		}finally{
			zkClient.close();
		}
	}
	
	public static void main(String[] args) {
		
		String regex = "^(\\d{11})|(0431\\d{8})|(0432\\d{8})|([a-zA-Z]{4}\\d{12})$";
		boolean flag= Pattern.matches(regex, "hello");
		System.out.println(flag);
	}
	
}
