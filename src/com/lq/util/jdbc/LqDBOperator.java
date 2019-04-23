package com.lq.util.jdbc;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 数据库链接池<br>
 * 默认读取lqjdbc.properties文件<br>
 * @author 吃人的肉
 * QQ:376870344<br>
 * email:liuqingrou@163.com
 */
public class LqDBOperator {
	protected static Map<String,Properties> dataSourceNameMap=null;
	protected static Map<String,LqDBOperatores> dsWebgames = new HashMap<String, LqDBOperatores>();
	protected static ComboPooledDataSource dsWebgame = new ComboPooledDataSource();
	protected static LqConfig conf = null;
	protected static String driverClassName="";
	protected static String url="";
	protected static String username="";
	protected static String pwd="";
	protected static String pageSize="";
	protected static String groupPageSize="";
	protected static String sqlLog="";
	protected static String sqlSuccessTime="";
	static {
		if (conf == null) {
			conf = new LqConfig();
			//conf.init("lqjdbc.properties");
			//dsWebgames
		}
		driverClassName=conf.getString("DriverClassName");
		try {
			dsWebgame.setDriverClass(driverClassName);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		// 参数由 Config 类根据配置文件读取
		pageSize=conf.getString("PageSize");
		groupPageSize=conf.getString("GroupPageSize");
		sqlLog=conf.getString("SqlLog");
		sqlSuccessTime=conf.getString("SqlSuccessTime");
		url=conf.getString("AllUrl").split("\\*\\*\\*")[0];
		dsWebgame.setJdbcUrl(url);// 设置JDBC的URL
		username=conf.getString("AllUrl").split("\\*\\*\\*")[1];
		dsWebgame.setUser(username);// 设置数据库的登录用户名
		pwd=conf.getString("AllUrl").split("\\*\\*\\*")[2];
		dsWebgame.setPassword(pwd);// 设置数据库的登录用户密码
		Integer MaxPoolSize=Integer.valueOf(conf.getString("MaxPoolSize"));
		dsWebgame.setMaxPoolSize(MaxPoolSize);// 设置连接池的最大连接数
		Integer MinPoolSize=Integer.valueOf(conf.getString("MinPoolSize"));
		dsWebgame.setMinPoolSize(MinPoolSize);// 设置连接池的最小连接数
		Integer InitialPoolSize=Integer.valueOf(conf.getString("InitialPoolSize"));
		dsWebgame.setInitialPoolSize(InitialPoolSize);//设置连接池的初始化连接数
		Integer NumHelperThreads=Integer.valueOf(conf.getString("NumHelperThreads"));
		dsWebgame.setNumHelperThreads(NumHelperThreads);//设置线程数量，默认是3
		dsWebgame.setAutoCommitOnClose(false);//连接关闭时默认将所有未提交的操作回滚
		dsWebgame.setTestConnectionOnCheckin(true);// 数据库断开后是否自动重连
		dsWebgame.setIdleConnectionTestPeriod(60);// 重连时间60秒
		dsWebgame.setMaxStatements(0);// 有时候会产生死锁，将maxStatements设置为0
		dsWebgame.setAcquireIncrement(3);//当连接池中的连接耗尽的时候c3p0一次同时获取的连接数
		dsWebgame.setIdleConnectionTestPeriod(60);//每60秒检查所有连接池中的空闲连接
		dsWebgame.setMaxIdleTime(60);//最大空闲时间,60秒内未使用则连接被丢弃
		dsWebgame.setCheckoutTimeout(10000);//当连接池用完时客户端调用getConnection()后等待获取新连接的时间，超时后将抛出SQLException,如设为0则无限期等待。单位毫秒。
		try {
			dsWebgame.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private LqDBOperator(){}
	
	

//	private static long c=0;
//	private static Lock  lock = new ReentrantLock();// 锁  
//	private static void main(String[] args) throws Exception {
//		for (int i = 0; i < 1000; i++) {
//			new Thread(){
//				public void run() {
//					System.out.println(new Date());
//					//List list=BaseDao.getJdbcTemplate().queryForList("select id from micro_blog_user limit 0,100");
//					//List list=BaseDao.getJdbcTemplate().queryForList("select id,title from zzy_seed limit 0,100");
//					//List list1=Jdbc.find("select id from micro_blog_user limit 0,10");
////					lock.lock();
//					c+=1;
////					lock.unlock();
//					run();
//				};
//			}.start();
//		}
//		while (true) {
//			System.err.println("num_connections: " + dsWebgame.getNumConnectionsDefaultUser());
//			System.err.println("num_busy_connections: " + dsWebgame.getNumBusyConnectionsDefaultUser());
//			System.err.println("num_idle_connections: " + dsWebgame.getNumIdleConnectionsDefaultUser());
//			System.err.println("-------------------------"+c+"-----------------------------------");
//			Thread.sleep(1000);
//		}
//	}
}
