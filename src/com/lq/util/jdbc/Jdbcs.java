package com.lq.util.jdbc;

import java.sql.Connection;
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
public class Jdbcs{

	protected Logger log = Logger.getLogger(Jdbcs.class);
	
	private LqDBOperatores dsWebgame;
	
	private Jdbcs(){}
	
	protected Jdbcs(LqDBOperatores dsWebgame){
		this.dsWebgame=dsWebgame;
	}
	
	public Connection getCon(){
		try {
			return dsWebgame.dsWebgame.getConnection();
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
	public int execute(String sql){
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
	public int execute(String sql,Object... obj){
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
	public Page findPage(String sql,Integer pageNumber,Integer pageSize,Long totalCount){
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
	public Page findPage(String sql,Integer pageNumber,Integer pageSize,Long totalCount,Object... obj){
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
	public Page findPage(String sql,Integer pageNumber,Integer pageSize,String sqlCount,Object... obj){
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
	public <T> List<T> find(String sql,Class<T> cls){
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
	public <T> List<T> find(String sql,Class<T> cls,Object... obj){
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
	public List<Map> find(String sql){
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
	public List<Map> find(String sql,Object... obj){
		List list=null;
		LqJdbcFactory jdbc=new LqJdbcFactory(getCon());
		list = jdbc.find(sql,obj);
		jdbc.close();
		return list;
	}
	
	public int save(Object obj){
		LqJdbcFactory jdbc=new LqJdbcFactory(getCon());
		int b=jdbc.save(obj);
		jdbc.close();
		return b;
	}
	public int update(Object obj){
		LqJdbcFactory jdbc=new LqJdbcFactory(getCon());
		int b=jdbc.update(obj);
		jdbc.close();
		return b;
	}
	public int delete(Object obj){
		LqJdbcFactory jdbc=new LqJdbcFactory(getCon());
		int b=jdbc.delete(obj);
		jdbc.close();
		return b;
	}
	
	/**
	 * 操作结果集
	 * @param resultSet
	 */
	public void operationResultSet(LqResultSet resultSet){
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
	public int shiwu(LqShiWu shiWu){
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
	public int[] piliang(String sql,LqPiLiang piLiang){
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
    public void createEntity(String path,String tableName,String url,String driver,String privateKey){
    	new LqGoEntity(path,tableName,url,driver,privateKey);
 		System.out.println("生成实体类成功!");
    }
    
    /**
     * 生成表对应实体类
     * @param path 实体类生成在哪个包下面   如:路径 com.lq.entity
     * @param tableName 表名
     * @param privateKey 主键,无主键设成唯一标识符的字段
     */
   public void createEntity(String path,String tableName,String privateKey){
	   new LqGoEntity(path,tableName,privateKey);
	   System.out.println("生成实体类成功!");
   }
   
   
   
	/**
	* 生成SQL语句
	* @param tableName 表名
	* @param privateKey 主键或是编号
	* @return 0:插入语句,1修改语句,2参数
	*/
	public List createSQL(String tableName,String url,String driver,String privateKey){
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
	public List createSQL(String tableName,String privateKey){
		LqGoInsertAndUpdateAndSelect a=new LqGoInsertAndUpdateAndSelect(tableName,privateKey);
		List list=new ArrayList();
		list.add(a.insertStr);
		list.add(a.insertObj);
		list.add(a.updateStr);
		list.add(a.updateObj);
		return list;
	}

}
