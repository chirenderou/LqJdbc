package com.lq.util.jdbc;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Properties;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 数据库链接池<br>
 * 默认读取lqjdbc.properties文件<br>
 * @author 吃人的肉
 * QQ:376870344<br>
 * email:liuqingrou@163.com
 */
public class LqDBOperatores {
	protected ComboPooledDataSource dsWebgame = new ComboPooledDataSource();
	protected String driverClassName="";
	protected String url="";
	protected String username="";
	protected String pwd="";
	protected String pageSize="";
	protected String groupPageSize="";
	protected String sqlLog="";
	protected String sqlSuccessTime="";
	
	private LqDBOperatores(){}

	protected LqDBOperatores(Properties props){
		driverClassName=props.getProperty("DriverClassName");
		try {
			dsWebgame.setDriverClass(driverClassName);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		// 参数由 Config 类根据配置文件读取
		pageSize=props.getProperty("PageSize".toLowerCase(),"15");
		groupPageSize=props.getProperty("GroupPageSize".toLowerCase(),"5");
		sqlLog=props.getProperty("SqlLog".toLowerCase(),"true");
		sqlSuccessTime=props.getProperty("SqlSuccessTime".toLowerCase(),"true");
		url=props.getProperty("AllUrl".toLowerCase()).split("\\*\\*\\*")[0];
		dsWebgame.setJdbcUrl(url);// 设置JDBC的URL
		username=props.getProperty("AllUrl".toLowerCase()).split("\\*\\*\\*")[1];
		dsWebgame.setUser(username);// 设置数据库的登录用户名
		pwd=props.getProperty("AllUrl".toLowerCase()).split("\\*\\*\\*")[2];
		dsWebgame.setPassword(pwd);// 设置数据库的登录用户密码
		Integer MaxPoolSize=Integer.valueOf(props.getProperty("MaxPoolSize".toLowerCase(),"100"));
		dsWebgame.setMaxPoolSize(MaxPoolSize);// 设置连接池的最大连接数
		Integer MinPoolSize=Integer.valueOf(props.getProperty("MinPoolSize".toLowerCase(),"5"));
		dsWebgame.setMinPoolSize(MinPoolSize);// 设置连接池的最小连接数
		Integer InitialPoolSize=Integer.valueOf(props.getProperty("InitialPoolSize".toLowerCase(),"5"));
		dsWebgame.setInitialPoolSize(InitialPoolSize);//设置连接池的初始化连接数
		Integer NumHelperThreads=Integer.valueOf(props.getProperty("NumHelperThreads".toLowerCase(),"20"));
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
}
