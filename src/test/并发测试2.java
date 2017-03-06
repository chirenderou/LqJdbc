package test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lq.util.jdbc.Jdbc;
import com.lq.util.jdbc.LqJdbcFactory;
import com.lq.util.jdbc.LqPiLiang;

public class 并发测试2 {
	public static ExecutorService es=Executors.newFixedThreadPool(50);
	public static CountDownLatch cdl=new CountDownLatch(50);
	public static final String sql="insert into test (name)values(?)";
	public static class lq implements Runnable{
		public void run() {
			Jdbc.piliang(sql,new LqPiLiang() {
				public void piliang(LqJdbcFactory jdbc) {
					for (int i = 0; i < 10000; i++) {
						jdbc.executeS(new Object[]{i+""});
					}
				}
			});
			cdl.countDown();
		}
	}
	
	
	public static void main(String[] args) {
		lq l=new lq();
		for (int i = 0; i < 10; i++) {
			es.submit(l);
		}
	}
	
}
