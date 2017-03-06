package com.lq.util.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * JDBC工厂<br>
 * @author 吃人的肉
 * QQ:376870344<br>
 * email:liuqingrou@163.com
 */
public class Jdbc{

	protected static Logger log = Logger.getLogger(Jdbc.class);
	
	private Jdbc(){}
	
	public static Jdbcs getDS(String dsName){
		LqDBOperatores dbs=null;
		if (LqDBOperator.dsWebgames.get(dsName.toLowerCase())==null) {
			dbs=new LqDBOperatores(LqDBOperator.dataSourceNameMap.get(dsName.toLowerCase()));
			LqDBOperator.dsWebgames.put(dsName.toLowerCase(), dbs);
		}else{
			dbs=LqDBOperator.dsWebgames.get(dsName.toLowerCase());
		}
		Jdbcs j=new Jdbcs(dbs);
		return j;
	}
	
	
	/**
	 * 数据库语句分类--SQL
	 * @param databasesSelectInterface
	 * @return
	 */
	public static String sql(DatabasesInterface databasesSelectInterface){
		if (LqDBOperator.driverClassName.equalsIgnoreCase("com.microsoft.sqlserver.jdbc.SQLServerDriver")) {
        	//SQL SERVER
			return databasesSelectInterface.sqlserver();
		}else if(LqDBOperator.driverClassName.equalsIgnoreCase("com.mysql.jdbc.Driver")){
			//MY SQL
			return databasesSelectInterface.mysql();
		}else if(LqDBOperator.driverClassName.equalsIgnoreCase("oracle.jdbc.driver.OracleDriver")){
			//Oracle
			return databasesSelectInterface.oracle();
		}
		return null;
	}
	
	/**
	 * 数据库语句分类--SQL
	 * @param databasesSelectInterface
	 * @return
	 */
	public static Object sql(DatabasesInterfaceForObject databasesInterfaceForObject){
		if (LqDBOperator.driverClassName.equalsIgnoreCase("com.microsoft.sqlserver.jdbc.SQLServerDriver")) {
        	//SQL SERVER
			return databasesInterfaceForObject.sqlserver();
		}else if(LqDBOperator.driverClassName.equalsIgnoreCase("com.mysql.jdbc.Driver")){
			//MY SQL
			return databasesInterfaceForObject.mysql();
		}else if(LqDBOperator.driverClassName.equalsIgnoreCase("oracle.jdbc.driver.OracleDriver")){
			//Oracle
			return databasesInterfaceForObject.oracle();
		}
		return null;
	}
	
	/**
	 * 数据库语句分类--SQL
	 * @param databasesSelectInterface
	 * @return
	 */
	public static String sql(String dsName,DatabasesInterface databasesSelectInterface){
		if (dsName==null) {
			//抛异常
			StringBuffer eStringBuffer=new StringBuffer();
			eStringBuffer.append("数据源名称不能为NULL！！");
			try {
				throw new LqJdbcException(eStringBuffer.toString());
			} catch (LqJdbcException e) {
				log.error("LqJdbc",e);
				e.printStackTrace();
			}
		}else{
			LqDBOperatores dbo=LqDBOperator.dsWebgames.get(dsName.toLowerCase());
			if (dbo==null) {
				//抛异常
				StringBuffer eStringBuffer=new StringBuffer();
				eStringBuffer.append("这个《").append(dsName).append("》数据源不存在！！");
				try {
					throw new LqJdbcException(eStringBuffer.toString());
				} catch (LqJdbcException e) {
					log.error("LqJdbc",e);
					e.printStackTrace();
				}
			}else{
				if (dbo.driverClassName.equalsIgnoreCase("com.microsoft.sqlserver.jdbc.SQLServerDriver")) {
					//SQL SERVER
					return databasesSelectInterface.sqlserver();
				}else if(dbo.driverClassName.equalsIgnoreCase("com.mysql.jdbc.Driver")){
					//MY SQL
					return databasesSelectInterface.mysql();
				}else if(dbo.driverClassName.equalsIgnoreCase("oracle.jdbc.driver.OracleDriver")){
					//Oracle
					return databasesSelectInterface.oracle();
				}
			}
		}
		return null;
	}
	
	/**
	 * 数据库语句分类--SQL
	 * @param databasesSelectInterface
	 * @return
	 */
	public static Object sql(String dsName,DatabasesInterfaceForObject databasesInterfaceForObject){
		if (dsName==null) {
			//抛异常
			StringBuffer eStringBuffer=new StringBuffer();
			eStringBuffer.append("数据源名称不能为NULL！！");
			try {
				throw new LqJdbcException(eStringBuffer.toString());
			} catch (LqJdbcException e) {
				log.error("LqJdbc",e);
				e.printStackTrace();
			}
		}else{
			LqDBOperatores dbo=LqDBOperator.dsWebgames.get(dsName.toLowerCase());
			if (dbo==null) {
				//抛异常
				StringBuffer eStringBuffer=new StringBuffer();
				eStringBuffer.append("这个《").append(dsName).append("》数据源不存在！！");
				try {
					throw new LqJdbcException(eStringBuffer.toString());
				} catch (LqJdbcException e) {
					log.error("LqJdbc",e);
					e.printStackTrace();
				}
			}else{
				if (dbo.driverClassName.equalsIgnoreCase("com.microsoft.sqlserver.jdbc.SQLServerDriver")) {
					//SQL SERVER
					return databasesInterfaceForObject.sqlserver();
				}else if(dbo.driverClassName.equalsIgnoreCase("com.mysql.jdbc.Driver")){
					//MY SQL
					return databasesInterfaceForObject.mysql();
				}else if(dbo.driverClassName.equalsIgnoreCase("oracle.jdbc.driver.OracleDriver")){
					//Oracle
					return databasesInterfaceForObject.oracle();
				}
			}
		}
		return null;
	}
	
	/**
	 * 总链接数
	 * @return
	 */
	public static long getNumConnectionsDefaultUser(){
		return getNumConnectionsDefaultUser(null);
	}
	
	/**
	 * 总链接数
	 * @return
	 */
	public static long getNumConnectionsDefaultUser(String dsName){
		if (dsName==null) {
			try {
				return LqDBOperator.dsWebgame.getNumConnectionsDefaultUser();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			LqDBOperatores dbs=null;
			if (LqDBOperator.dsWebgames.get(dsName.toLowerCase())==null) {
				dbs=new LqDBOperatores(LqDBOperator.dataSourceNameMap.get(dsName.toLowerCase()));
				LqDBOperator.dsWebgames.put(dsName.toLowerCase(), dbs);
			}else{
				dbs=LqDBOperator.dsWebgames.get(dsName.toLowerCase());
			}
			try {
				return dbs.dsWebgame.getNumConnectionsDefaultUser();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	
	/**
	 * 正在运行状态的连接数
	 * @return
	 */
	public static long getNumBusyConnectionsDefaultUser(){
		return getNumBusyConnectionsDefaultUser(null);
	}
	
	/**
	 * 正在运行状态的连接数
	 * @return
	 */
	public static long getNumBusyConnectionsDefaultUser(String dsName){
		if (dsName==null) {
			try {
				return LqDBOperator.dsWebgame.getNumBusyConnectionsDefaultUser();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			LqDBOperatores dbs=null;
			if (LqDBOperator.dsWebgames.get(dsName.toLowerCase())==null) {
				dbs=new LqDBOperatores(LqDBOperator.dataSourceNameMap.get(dsName.toLowerCase()));
				LqDBOperator.dsWebgames.put(dsName.toLowerCase(), dbs);
			}else{
				dbs=LqDBOperator.dsWebgames.get(dsName.toLowerCase());
			}
			try {
				return dbs.dsWebgame.getNumBusyConnectionsDefaultUser();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	
	
	/**
	 * 空闲连接数
	 * @return
	 */
	public static long getNumIdleConnectionsDefaultUser(){
		return getNumIdleConnectionsDefaultUser(null);
	}
	
	/**
	 * 空闲连接数
	 * @return
	 */
	public static long getNumIdleConnectionsDefaultUser(String dsName){
		if (dsName==null) {
			try {
				return LqDBOperator.dsWebgame.getNumBusyConnectionsDefaultUser();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			LqDBOperatores dbs=null;
			if (LqDBOperator.dsWebgames.get(dsName.toLowerCase())==null) {
				dbs=new LqDBOperatores(LqDBOperator.dataSourceNameMap.get(dsName.toLowerCase()));
				LqDBOperator.dsWebgames.put(dsName.toLowerCase(), dbs);
			}else{
				dbs=LqDBOperator.dsWebgames.get(dsName.toLowerCase());
			}
			try {
				return dbs.dsWebgame.getNumIdleConnectionsDefaultUser();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	
	public static Connection getCon(){
		try {
			return LqDBOperator.dsWebgame.getConnection();
		} catch (SQLException e) {
			log.error("LqJdbc",e);
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * EXEC
	 * @param sql
	 * @return
	 */
	@Deprecated
	public static int execute(String sql){
		int b=0;
		LqJdbcFactory jdbc=new LqJdbcFactory(getCon());
		b = jdbc.execute(sql);
		jdbc.close();
		return b;
	}
	/**
	 * EXEC
	 * @param sql
	 * @return
	 */
	public static int execute(String sql,Object... obj){
		int b=0;
		LqJdbcFactory jdbc=new LqJdbcFactory(getCon());
		b = jdbc.execute(sql,obj);
		jdbc.close();
		return b;
	}
	
	/**
	 * 分页
	 * @param sql
	 * @param pageNumber
	 * @param pageSize
	 * @param totalCount
	 * @return
	 */
	@Deprecated
	public static Page findPage(String sql,Integer pageNumber,Integer pageSize,Long totalCount){
		LqJdbcFactory jdbc=new LqJdbcFactory(getCon());
		Page page= jdbc.find(sql, pageNumber, pageSize, totalCount);
		jdbc.close();
		return page;
	}
	/**
	 * 分页《安全》<br>
	 * SQL_SERVER_2005分页:SELECT ROW_NUMBER() OVER (ORDER BY id) AS RowNumber,* from tableName
	 * @param sql
	 * @param pageNumber1 当前页数
	 * @param pageSize 一页所显示的记录数
	 * @param totalCount 总记录数
	 * @return
	 */
	public static Page findPage(String sql,Integer pageNumber,Integer pageSize,Long totalCount,Object... obj){
		LqJdbcFactory jdbc=new LqJdbcFactory(getCon());
		Page page= jdbc.find(sql, pageNumber, pageSize, totalCount,obj);
		jdbc.close();
		return page;
	}
	
	/**
	 * 分页《安全》<br>
	 * SQL_SERVER_2005分页:SELECT ROW_NUMBER() OVER (ORDER BY id) AS RowNumber,* from tableName
	 * @param sql
	 * @param pageNumber1 当前页数
	 * @param pageSize 一页所显示的记录数
	 * @param sqlCount 得总记录数SQL
	 * @return
	 */
	public static Page findPage(String sql,Integer pageNumber,Integer pageSize,String sqlCount,Object... obj){
		LqJdbcFactory jdbc=new LqJdbcFactory(getCon());
		Page page= jdbc.find(sql, pageNumber, pageSize, sqlCount,obj);
		jdbc.close();
		return page;
	}
	
	/**
	 * 查询<有BUG>
	 * @param sql
	 * @param cls
	 * @return
	 */
	@Deprecated
	public static <T> List<T> find(String sql,Class<T> cls){
		List list=null;
		LqJdbcFactory jdbc=new LqJdbcFactory(getCon());
		list = jdbc.find(sql,cls);
		jdbc.close();
		return list;
	}
	
	/**
	 * 查询<有BUG>
	 * @param sql
	 * @param cls
	 * @param obj
	 * @return
	 */
	public static <T> List<T> find(String sql,Class<T> cls,Object... obj){
		List list=null;
		LqJdbcFactory jdbc=new LqJdbcFactory(getCon());
		list = jdbc.find(sql,cls,obj);
		jdbc.close();
		return list;
	}
	
	/**
	 * 查询
	 * @param sql
	 * @return
	 */
	@Deprecated
	public static List<Map> find(String sql){
		List list=null;
		LqJdbcFactory jdbc=new LqJdbcFactory(getCon());
		list = jdbc.find(sql);
		jdbc.close();
		return list;
	}
	/**
	 * 查询
	 * @param sql
	 * @return
	 */
	public static List<Map> find(String sql,Object... obj){
		List list=null;
		LqJdbcFactory jdbc=new LqJdbcFactory(getCon());
		list = jdbc.find(sql,obj);
		jdbc.close();
		return list;
	}
	
	public static int save(Object obj){
		LqJdbcFactory jdbc=new LqJdbcFactory(Jdbc.getCon());
		int b=jdbc.save(obj);
		jdbc.close();
		return b;
	}
	public static int update(Object obj){
		LqJdbcFactory jdbc=new LqJdbcFactory(Jdbc.getCon());
		int b=jdbc.update(obj);
		jdbc.close();
		return b;
	}
	public static int delete(Object obj){
		LqJdbcFactory jdbc=new LqJdbcFactory(Jdbc.getCon());
		int b=jdbc.delete(obj);
		jdbc.close();
		return b;
	}
	
	/**
	 * 操作结果集
	 * @param resultSet
	 */
	public static void operationResultSet(LqResultSet resultSet){
		LqJdbcFactory jdbc=new LqJdbcFactory(getCon());
		try {
			resultSet.getResultSet(jdbc);
		} catch (Exception e) {
			try {jdbc.conn.rollback();} catch (SQLException e1) {
				log.error("LqJdbc",e1);
				e1.printStackTrace();
			}
			log.error("LqJdbc",e);
			e.printStackTrace();
		}finally{
			jdbc.close();
		}
	}

	
	/**
	 * 事务操作
	 * @param shiWu
	 * @return
	 */
	public static int shiwu(LqShiWu shiWu){
		LqJdbcFactory jdbc=new LqJdbcFactory(getCon());
		try {
			jdbc.conn.setAutoCommit(false);//禁止自动提交，设置回滚点
			shiWu.shiwu(jdbc);
			if (jdbc.shiB!=null) {
				try {jdbc.conn.rollback();} catch (SQLException e1) {
					log.error("LqJdbc",e1);
					e1.printStackTrace();
				}
			}else {
				jdbc.conn.commit();
				jdbc.conn.setAutoCommit(true);
			}
		} catch (Exception e) {
			try {jdbc.conn.rollback();} catch (SQLException e1) {
				log.error("LqJdbc",e1);
				e1.printStackTrace();
			}
			log.error("LqJdbc",e);
			e.printStackTrace();
		}finally{
			jdbc.close();
		}
		if (jdbc.shiB==null) {
			jdbc.shiB=1;
		}
		return jdbc.shiB;
	}
	
	/**
	 * 批量操作
	 * @param sql
	 * @param piLiang
	 * @return
	 */
	public static int[] piliang(String sql,LqPiLiang piLiang){
		LqJdbcFactory jdbc=new LqJdbcFactory(getCon());
		int[] c = null;
		try {
			jdbc.conn.setAutoCommit(false);
			jdbc.pstmt = jdbc.conn.prepareStatement(sql);
			if (LqDBOperator.sqlLog.equalsIgnoreCase("true")) {
				StringBuffer sqlSB=new StringBuffer("SQL语句:");
				log.info(sqlSB.append(sql.replaceAll("\r\n", " ")));
			}
			piLiang.piliang(jdbc);
			long timeStart = System.currentTimeMillis();
			c=jdbc.pstmt.executeBatch();
			jdbc.conn.commit();
			jdbc.conn.setAutoCommit(true);
			if (LqDBOperator.sqlSuccessTime.equalsIgnoreCase("true")) {
				log.info("查询时间：" + (System.currentTimeMillis() - timeStart) + "ms");
			}
		} catch (Exception e) {
			log.error("LqJdbc",e);
			e.printStackTrace();
		}finally{
			jdbc.close();
		}
		return c;
	}
	
	
	/**
     * 生成表对应实体类
     * @param path 实体类生成在哪个包下面   如:路径 com.lq.entity
     * @param tableName 表名
     * @param url 连接
     * @param driver 驱动名
     * @param privateKey 主键,无主键设成唯一标识符的字段
     */
    public static void createEntity(String path,String tableName,String url,String driver,String privateKey){
    	new LqGoEntity(path,tableName,url,driver,privateKey);
 		System.out.println("生成实体类成功!");
    }
    
    /**
     * 生成表对应实体类
     * @param path 实体类生成在哪个包下面   如:路径 com.lq.entity
     * @param tableName 表名
     * @param privateKey 主键,无主键设成唯一标识符的字段
     */
   public static void createEntity(String path,String tableName,String privateKey){
	   new LqGoEntity(path,tableName,privateKey);
	   System.out.println("生成实体类成功!");
   }
   
   
   
	/**
	* 生成SQL语句
	* @param tableName 表名
	* @param privateKey 主键或是编号
	* @return 0:插入语句,1修改语句,2参数
	*/
	public static List createSQL(String tableName,String url,String driver,String privateKey){
		LqGoInsertAndUpdateAndSelect a=new LqGoInsertAndUpdateAndSelect(tableName,url,driver,privateKey);
		List list=new ArrayList();
		list.add(a.insertStr);
		list.add(a.insertObj);
		list.add(a.updateStr);
		list.add(a.updateObj);
		return list;
	}
	
	/**
	 * 生成SQL语句
	 * @param tableName 表名
	 * @param privateKey 主键或是编号
	 * @return 0:插入语句,1修改语句,2参数
	 */
	public static List createSQL(String tableName,String privateKey){
		LqGoInsertAndUpdateAndSelect a=new LqGoInsertAndUpdateAndSelect(tableName,privateKey);
		List list=new ArrayList();
		list.add(a.insertStr);
		list.add(a.insertObj);
		list.add(a.updateStr);
		list.add(a.updateObj);
		return list;
	}

}
