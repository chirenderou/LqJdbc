package test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lq.util.jdbc.Jdbc;

public class 并发测试 {
	public static ExecutorService es=Executors.newFixedThreadPool(10);
	public static CountDownLatch cdl=new CountDownLatch(10);
	public static final String sql="insert into test (name)values(?)";
	public static class lq implements Runnable{
		public void run() {
			Jdbc.execute(sql, new Object[]{System.currentTimeMillis()});
			cdl.countDown();
		}
	}
	
	
	public static void main(String[] args) {
		lq l=new lq();
		for (int i = 0; i < 10000; i++) {
			es.submit(l);
		}
	}
	
}
