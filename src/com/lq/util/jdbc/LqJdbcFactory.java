package com.lq.util.jdbc;

import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.log4j.Logger;

import oracle.sql.CLOB;


/**
 * JDBC工厂 
 * @author 吃人的肉
 * QQ:376870344<br>
 * email:liuqingrou@163.com
 */
public class LqJdbcFactory {
	//数据源连接参数
	public Connection conn = null;
	public Statement stmt = null;
	public PreparedStatement pstmt = null;
	protected Logger log = Logger.getLogger(LqJdbcFactory.class);
	public Integer shiB = null;//事务用到来判断回滚,不等于NULL就回滚
	public ResultSet resultSet=null;
	
	/**
	 * 1:回滚
	 */
	protected void g(){
		shiB=518;
	}
	
	/**
	 * 不用传参的CON
	 * 直接调用lqjdbc.properties
	 */
	public LqJdbcFactory(){
		try {
			conn=LqDBOperator.dsWebgame.getConnection();//连接池
		} catch (SQLException e1) {
			log.error("LqJdbc",e1);
			e1.printStackTrace();
		}
	}
	
	/**
	 * 传入con
	 * @param c
	 */
	public LqJdbcFactory(Connection c){
		conn=c;
	}
	
	/**
	 * 读取其它properties文件
	 * @param properties
	 */
	public LqJdbcFactory(String properties){
		LqConfig conf= new LqConfig(properties);
		try {
			Class.forName(conf.getString("DriverClassName"));
			conn = DriverManager.getConnection(conf.getString("AllUrl").split("\\*\\*\\*")[0],conf.getString("AllUrl").split("\\*\\*\\*")[1],conf.getString("AllUrl").split("\\*\\*\\*")[2]);
		} catch (Exception e) {
			log.error("LqJdbc",e);
			e.printStackTrace();
		}
	}
	
	/**
	 * 传入参数
	 * @param driverClassName 驱动名
	 * @param url URL格式见lqjdbc.properties
	 */
	public LqJdbcFactory(String driverClassName,String url){
		try {
			Class.forName(driverClassName);
			conn = DriverManager.getConnection(url.split("\\*\\*\\*")[0],url.split("\\*\\*\\*")[1],url.split("\\*\\*\\*")[2]);
		} catch (Exception e) {
			log.error("LqJdbc",e);
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询《安全》
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public  <T> List<T> find(String sql,Class<T> cls,Object... obj){
		int b=1;//是否关闭连接
		java.sql.ResultSet rs = null;
		ArrayList<T> list=null;
		try {
			list = new ArrayList<T>();
			pstmt = conn.prepareStatement(sql);
			if (obj!=null) {
				for (int i = 1; i <= obj.length; i++) {
					pstmt.setObject(i, obj[i-1]);
				}
			}
			rs = pstmt.executeQuery();
			log.info(sql.replaceAll("\r\n", " "));
			//ResultSetMetaData rsm = rs.getMetaData();// 获取数据库表结构
			//int col = rsm.getColumnCount();// 获取数据库的列数
			while (rs.next()) {
				T t = executeResultSet(cls, rs);
                list.add(t);
			}
		}catch(Exception e){
			g();
			log.error("LqJdbc",e);
			e.printStackTrace();
		}finally{
			if(b==1){
				if (rs!=null) {
					try {
						rs.close();
					} catch (SQLException e) {
						log.error("LqJdbc",e);
						e.printStackTrace();
					}
				}
			}
		}
		return list;
	}
	
	/**
	 * EXEC《安全》
	 * @param sql
	 * @param obj
	 * @return
	 */
	public int execute(String sql, Object... obj) {
		int b = 0;
		try {
			pstmt = conn.prepareStatement(sql);
			if (obj != null) {
				for (int i = 1; i <= obj.length; i++) {
					pstmt.setObject(i, obj[i - 1]);
				}
			}
			if (LqDBOperator.sqlLog.equalsIgnoreCase("true")) {
				insertSqlLog(sql, obj);
			}
			long timeStart = System.currentTimeMillis();
			b = pstmt.executeUpdate();
			if (LqDBOperator.sqlSuccessTime.equalsIgnoreCase("true")) {
				log.info("查询时间：" + (System.currentTimeMillis() - timeStart) + "ms");
			}
		} catch (SQLException e) {
			g();
			insertSqlLog(sql, obj);
			log.error("LqJdbc",e);
			e.printStackTrace();
		}
		return b;
	}
	
	/**
	 * EXEC批量《安全》
	 * @param obj
	 */
	public void executeS(Object... obj) {
		try {
			if (obj != null) {
				for (int i = 1; i <= obj.length; i++) {
					pstmt.setObject(i, obj[i - 1]);
				}
			}
			if (LqDBOperator.sqlLog.equalsIgnoreCase("true")) {
				insertObjLog(obj);
			}
			pstmt.addBatch();
		} catch (SQLException e) {
			g();
			insertObjLog(obj);
			log.error("LqJdbc",e);
			e.printStackTrace();
		}
	}
		
	/**
	 * 得到一个数据源(单调此方法需要在操作完成后关闭结果集)
	 * @param sql
	 * @param obj
	 * @return 文件流
	 */
	public ResultSet findResultSet(String sql,Object... obj){
		try {
			pstmt = conn.prepareStatement(sql);
			if (obj != null) {
				for (int i = 1; i <= obj.length; i++) {
					pstmt.setObject(i, obj[i - 1]);
				}
			}
			if (LqDBOperator.sqlLog.equalsIgnoreCase("true")) {
				insertSqlLog(sql, obj);
			}
			resultSet = pstmt.executeQuery();
		} catch (Exception e) {
			g();
			insertSqlLog(sql, obj);
			log.error("LqJdbc",e);
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e2) {
					log.error("LqJdbc",e2);
					e.printStackTrace();
				}
			}
			e.printStackTrace();
		}
		return resultSet;
	}
	
	/**
	 * 查询《安全》
	 * @param sql
	 * @param obj
	 * @return
	 */
	public List<ListOrderedMap> find(String sql, Object... obj) {
		int b = 1;// 是否关闭连接
		java.sql.ResultSet rs = null;
		ArrayList<ListOrderedMap> list = new ArrayList<ListOrderedMap>();
		try {
			pstmt = conn.prepareStatement(sql);
			if (obj != null) {
				for (int i = 1; i <= obj.length; i++) {
					pstmt.setObject(i, obj[i - 1]);
				}
			}
			if (LqDBOperator.sqlLog.equalsIgnoreCase("true")) {
				insertSqlLog(sql, obj);
			}
			long timeStart = System.currentTimeMillis();
			rs = pstmt.executeQuery();
			if (LqDBOperator.sqlSuccessTime.equalsIgnoreCase("true")) {
				log.info("查询时间：" + (System.currentTimeMillis() - timeStart) + "ms");
			}
			ResultSetMetaData rsm = rs.getMetaData();// 获取数据库表结构
			int col = rsm.getColumnCount();// 获取数据库的列数
			while (rs.next()) {
				ListOrderedMap rowData = new ListOrderedMap();
				for (int i = 1; i <= col; i++) {
					if (rsm.getColumnTypeName(i).toUpperCase().equals("BLOB")) {
						
					}else if (rsm.getColumnTypeName(i).toUpperCase().equals("CLOB")) {
						if (rs.getObject(i) == null){
							rowData.put(rsm.getColumnName(i).toLowerCase(), "");
						}else{
							CLOB clob=(CLOB) rs.getObject(i);
							Reader inStream = clob.getCharacterStream();
							char[] c = new char[(int) clob.length()];
							inStream.read(c);
							//data是读出并需要返回的数据，类型是String
							String data = new String(c);
							inStream.close();
							rowData.put(rsm.getColumnName(i).toLowerCase(),data);
						}
					}else if(rsm.getColumnTypeName(i).toUpperCase().equals("IMAGE")){
						
					}else {
						if (rs.getObject(i) == null)
							rowData.put(rsm.getColumnName(i).toLowerCase(), "");
						else
							rowData.put(rsm.getColumnName(i).toLowerCase(),rs.getObject(i));
					}
					
				}
				list.add(rowData);
			}
		} catch (Exception e) {
			g();
			insertSqlLog(sql, obj);
			log.error("LqJdbc",e);
			e.printStackTrace();
		} finally {
			if (b == 1) {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						log.error("LqJdbc",e);
						e.printStackTrace();
					}
				}
			}
		}
		return list;
	}
	
	/**
	 * 关闭
	 */
	public  void close(){
		if (stmt != null){
			try {
				stmt.close();
			} catch (SQLException e) {
				log.error("LqJdbc",e);
				e.printStackTrace();
			}
		}
		if (pstmt != null){
			try {
				pstmt.close();
			} catch (SQLException e) {
				log.error("LqJdbc",e);
				e.printStackTrace();
			}
		}
		if (resultSet!=null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				log.error("LqJdbc",e);
				e.printStackTrace();
			}
		}
		if (conn != null){
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("LqJdbc",e);
				e.printStackTrace();
			}
		}
		System.gc();
	}
	
	/**
	 * 分页《安全》<br>
	 * SQL_SERVER_2005分页:SELECT ROW_NUMBER() OVER (ORDER BY id) AS RowNumber,* from tableName
	 * @param sql
	 * @param pageNumber 当前页数
	 * @param pageSize 一页所显示的记录数
	 * @param totalCount 总记录数
	 * @return
	 */
	public <T> Page<T> find(String sql, Integer pageNumber, Integer pageSize,Long totalCount,Class<T> cls, Object... obj) {
		if (pageNumber == null || pageNumber<1 ) {
			pageNumber = 1;
		}
		if (totalCount <= 0) {
			return new Page<T>();
		}
		long totalPageCount = getTotalPageCount(totalCount, pageSize);
		if (pageNumber > totalPageCount) {
			//pageNumber = Long.valueOf(totalPageCount).intValue();
		}
		int startIndex = Page.getStartOfPage(pageNumber, pageSize);
		StringBuffer exeSql = new StringBuffer(sql);
		if (LqDBOperator.driverClassName.toLowerCase().indexOf("mysql")!=-1) {
			exeSql.append(" limit ").append(startIndex).append(",").append(pageSize);
		}else if (LqDBOperator.driverClassName.toLowerCase().indexOf("oracle")!=-1) {
			exeSql.append("select * from (select row_.*, rownum rownum_  from (").append(sql).append(") row_").append(" where rownum <= ").append(startIndex + pageSize).append(")").append(" where rownum_ >").append(startIndex);
		}else if(LqDBOperator.driverClassName.toLowerCase().indexOf("sqlserver")!=-1){
			/**
			 * 注解：首先利用Row_number()为table1表的每一行添加一个行号，给行号这一列取名'RowNumber' 在over()方法中将'RowNumber'做了升序排列
			 * 然后将'RowNumber'列 与table1表的所有列 形成一个表A
			 * 重点在where条件。假如当前页(currentPage)是第2页，每页显示10个数据(pageSzie)。那么第一页的数据就是第11-20条
			 * 所以为了显示第二页的数据，即显示第11-20条数据，那么就让RowNumber大于 10*(2-1) 即：页大小*(当前页-1)
			 */
			exeSql=new StringBuffer();
			int count=0;
			Pattern p=Pattern.compile("(order by){1}");
			Matcher m=p.matcher(sql.toLowerCase());
			while (m.find()) {
				count++;
			}
			int w=sql.toLowerCase().lastIndexOf("order by");
			if (w!=-1&&count>1) {
				exeSql.append("SELECT TOP ").append(pageSize).append(" * FROM ( ").append(sql.substring(0, w)).append(" ) as row_ WHERE RowNumber > ").append(pageSize*(pageNumber-1)).append(" "+sql.substring(w, sql.length()));
			}else {
				exeSql.append("SELECT TOP ").append(pageSize).append(" * FROM ( ").append(sql).append(" ) as row_ WHERE RowNumber > ").append(pageSize*(pageNumber-1));
			}
		}else if(LqDBOperator.driverClassName.toLowerCase().indexOf("sqlite")!=-1) {
			exeSql.append(" limit ").append(startIndex).append(",").append(pageSize);
		}else {
			try {
				StringBuffer eStringBuffer=new StringBuffer();
				eStringBuffer.append("不支持这个驱动的分页:").append(LqDBOperator.driverClassName);
				throw new LqJdbcException(eStringBuffer.toString());
			} catch (LqJdbcException e) {
				g();
				log.error("LqJdbc",e);
				e.printStackTrace();
			}
		}
		List<T> list =find(exeSql.toString(),cls,obj);
		return new Page<T>(startIndex, totalCount, pageSize, list,Page.DEFAULT_GROUP_PAGE_SIZE);
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
	public <T> Page<T> find(String sql,Integer pageNumber,Integer pageSize,String sqlCount,Class<T> cls,Object... obj){
		long totalCount=0;
		List<ListOrderedMap> listCount=find(sqlCount,obj);
		if (listCount.size()>0) {
			ListOrderedMap mapCount=listCount.get(0);
			if (mapCount.size()==1){
				totalCount=Long.valueOf(mapCount.values().toArray()[0].toString());
			}else {
				try {
					StringBuffer eStringBuffer=new StringBuffer();
					eStringBuffer.append("查询总记录数的SQL有问题,只能返回一个数量,不能返回多个字段.\r\n").append("SQL:").append(sqlCount);
					throw new LqJdbcException(eStringBuffer.toString());
				} catch (LqJdbcException e) {
					g();
					log.error("LqJdbc",e);
					e.printStackTrace();
				}
			}
		}
		return find(sql,pageNumber,pageSize,totalCount,cls,obj);
	}
	
	/**
	 * 分页《安全》<br>
	 * SQL_SERVER_2005分页:SELECT ROW_NUMBER() OVER (ORDER BY id) AS RowNumber,* from tableName
	 * @param sql
	 * @param pageNumber 当前页数
	 * @param pageSize 一页所显示的记录数
	 * @param totalCount 总记录数
	 * @return
	 */
	public Page<ListOrderedMap> find(String sql, Integer pageNumber, Integer pageSize,Long totalCount, Object... obj) {
		if (pageNumber == null || pageNumber<1 ) {
			pageNumber = 1;
		}
		if (totalCount <= 0) {
			return new Page<ListOrderedMap>();
		}
		long totalPageCount = getTotalPageCount(totalCount, pageSize);
		if (pageNumber > totalPageCount) {
			//pageNumber = Long.valueOf(totalPageCount).intValue();
		}
		int startIndex = Page.getStartOfPage(pageNumber, pageSize);
		StringBuffer exeSql = new StringBuffer(sql);
		if (LqDBOperator.driverClassName.toLowerCase().indexOf("mysql")!=-1) {
			exeSql.append(" limit ").append(startIndex).append(",").append(pageSize);
		}else if (LqDBOperator.driverClassName.toLowerCase().indexOf("oracle")!=-1) {
			exeSql.append("select * from (select row_.*, rownum rownum_  from (").append(sql).append(") row_").append(" where rownum <= ").append(startIndex + pageSize).append(")").append(" where rownum_ >").append(startIndex);
		}else if(LqDBOperator.driverClassName.toLowerCase().indexOf("sqlserver")!=-1){
			/**
			 * 注解：首先利用Row_number()为table1表的每一行添加一个行号，给行号这一列取名'RowNumber' 在over()方法中将'RowNumber'做了升序排列
			 * 然后将'RowNumber'列 与table1表的所有列 形成一个表A
			 * 重点在where条件。假如当前页(currentPage)是第2页，每页显示10个数据(pageSzie)。那么第一页的数据就是第11-20条
			 * 所以为了显示第二页的数据，即显示第11-20条数据，那么就让RowNumber大于 10*(2-1) 即：页大小*(当前页-1)
			 */
			exeSql=new StringBuffer();
			int count=0;
			Pattern p=Pattern.compile("(order by){1}");
			Matcher m=p.matcher(sql.toLowerCase());
			while (m.find()) {
				count++;
			}
			int w=sql.toLowerCase().lastIndexOf("order by");
			if (w!=-1&&count>1) {
				exeSql.append("SELECT TOP ").append(pageSize).append(" * FROM ( ").append(sql.substring(0, w)).append(" ) as row_ WHERE RowNumber > ").append(pageSize*(pageNumber-1)).append(" "+sql.substring(w, sql.length()));
			}else {
				exeSql.append("SELECT TOP ").append(pageSize).append(" * FROM ( ").append(sql).append(" ) as row_ WHERE RowNumber > ").append(pageSize*(pageNumber-1));
			}
		}else if(LqDBOperator.driverClassName.toLowerCase().indexOf("sqlite")!=-1) {
			exeSql.append(" limit ").append(startIndex).append(",").append(pageSize);
		}else {
			try {
				StringBuffer eStringBuffer=new StringBuffer();
				eStringBuffer.append("不支持这个驱动的分页:").append(LqDBOperator.driverClassName);
				throw new LqJdbcException(eStringBuffer.toString());
			} catch (LqJdbcException e) {
				g();
				log.error("LqJdbc",e);
				e.printStackTrace();
			}
		}
		List<ListOrderedMap> list =find(exeSql.toString(),obj);
		return new Page<ListOrderedMap>(startIndex, totalCount, pageSize, list,Page.DEFAULT_GROUP_PAGE_SIZE);
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
	public Page<ListOrderedMap> find(String sql,Integer pageNumber,Integer pageSize,String sqlCount,Object... obj){
		long totalCount=0;
		List<ListOrderedMap> listCount=find(sqlCount,obj);
		if (listCount.size()>0) {
			ListOrderedMap mapCount=listCount.get(0);
			if (mapCount.size()==1){
				totalCount=Long.valueOf(mapCount.values().toArray()[0].toString());
			}else {
				try {
					StringBuffer eStringBuffer=new StringBuffer();
					eStringBuffer.append("查询总记录数的SQL有问题,只能返回一个数量,不能返回多个字段.\r\n").append("SQL:").append(sqlCount);
					throw new LqJdbcException(eStringBuffer.toString());
				} catch (LqJdbcException e) {
					g();
					log.error("LqJdbc",e);
					e.printStackTrace();
				}
			}
		}
		return find(sql,pageNumber,pageSize,totalCount,obj);
	}
	
	/**
	 * 取总页数.
	 */
	private long getTotalPageCount(long totalCount, int pageSize) {
		if (totalCount % pageSize == 0)
			return totalCount / pageSize;
		else
			return totalCount / pageSize + 1;
	}
	
	//创建语句类型；  
    private final int entitySave=0;
    private final int entityUpdate=1;
    private final int entityDelete=2;
	
    public int save(Object obj){
    	return setObject(obj,entitySave);
    }
    public int update(Object obj){
    	return setObject(obj, entityUpdate);
    }
    public int delete(Object obj){
    	return setObject(obj, entityDelete);
    }
    
    
    protected int setObject(Object obj,int type){
    	String tableName="";//表名
    	//-----------------------------------插入--------------------------
 		String str1 = "";//有几个属性。
 		String str2 = "";//有几个待插入的位置
 		//-----------------------------------更新--------------------------
 		String strUpdateSql="";
 		String strUpdateWhereSql="";
 		Object strObjectUpdate = null;
 		//-----------------------------------删除--------------------------
 		String strDelWhereSql="";
 		Object strDelObject=null;
 		
 		List<Object> objList=new ArrayList<Object>();
    	Class<? extends Object> cla=obj.getClass();
    	Annotation an=cla.getAnnotation(Table.class);
    	if (an==null) {
    		try {
    			StringBuffer eStringBuffer=new StringBuffer();
    			eStringBuffer.append("没有给实体类").append(obj).append("配制注解@Table(name=)__name=表名");
				throw new LqJdbcException(eStringBuffer.toString());
			} catch (LqJdbcException e) {
				g();
				log.error("LqJdbc",e);
				e.printStackTrace();
			}
			return 0;
		}
    	for (Method method : an.annotationType().getDeclaredMethods()) {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            Object invoke = null;
			try {
				invoke = method.invoke(an);
			} catch (IllegalAccessException e) {g();
				log.error("LqJdbc",e);
				e.printStackTrace();
			} catch (IllegalArgumentException e) {g();
				log.error("LqJdbc",e);
				e.printStackTrace();
			} catch (InvocationTargetException e) {g();
				log.error("LqJdbc",e);
				e.printStackTrace();
			}
			if (method.getName().equals("name")) {
				tableName=invoke+"";
			}
            //System.out.println("invoke methd " + method.getName() + " result:" + invoke);
//            if (invoke.getClass().isArray()) {
//                Object[] temp = (Object[]) invoke;
//                for (Object o : temp) {
//                    System.out.println(o);
//                }
//            }
        }
    	
        for (int i = 0; i < cla.getMethods().length; i++) {
        	Method m=cla.getMethods()[i];
        	String zdm=m.getName();
        	if (zdm.substring(0,3).equals("get")&&!zdm.equals("getClass")) {
        		if(type==entitySave){
        			Sql anSql=m.getAnnotation(Sql.class);
        	    	if (anSql==null) {//没有配制不管它,不是字段
						continue;
					}
        			str1 += anSql.column() + ",";
        			str2 += "? ,";
        			try {
    					objList.add(m.invoke(obj));
    				} catch (IllegalAccessException e1) {g();
    					log.error("LqJdbc",e1);
    					e1.printStackTrace();
    				} catch (IllegalArgumentException e1) {g();
    					log.error("LqJdbc",e1);
    					e1.printStackTrace();
    				} catch (InvocationTargetException e1) {g();
    					log.error("LqJdbc",e1);
    					e1.printStackTrace();
    				}
        		}else if(type==entityUpdate){
        			Sql anSql=m.getAnnotation(Sql.class);
        	    	if (anSql==null) {//没有配制不管它,不是字段
						continue;
					}
        			Annotation anId=m.getAnnotation(Id.class);
        	    	if (anId!=null) {//ID
						strUpdateWhereSql+=anSql.column()+"=?";
						try {
							strObjectUpdate=m.invoke(obj);
						} catch (IllegalAccessException e) {g();
							log.error("LqJdbc",e);
							e.printStackTrace();
						} catch (IllegalArgumentException e) {g();
							log.error("LqJdbc",e);
							e.printStackTrace();
						} catch (InvocationTargetException e) {g();
							log.error("LqJdbc",e);
							e.printStackTrace();
						}
					}else {
						strUpdateSql+=anSql.column()+"=?,";
						try {
							objList.add(m.invoke(obj));
						} catch (IllegalAccessException e1) {g();
							log.error("LqJdbc",e1);
							e1.printStackTrace();
						} catch (IllegalArgumentException e1) {g();
							log.error("LqJdbc",e1);
							e1.printStackTrace();
						} catch (InvocationTargetException e1) {g();
							log.error("LqJdbc",e1);
							e1.printStackTrace();
						}
					}
        			
        		}else if(type==entityDelete){
        			Sql anSql=m.getAnnotation(Sql.class);
        			Annotation anId=m.getAnnotation(Id.class);
        	    	if (anId!=null) {//ID
						strDelWhereSql+=anSql.column()+"=?";
						try {
							strDelObject=m.invoke(obj);
						} catch (IllegalAccessException e) {g();
							log.error("LqJdbc",e);
							e.printStackTrace();
						} catch (IllegalArgumentException e) {g();
							log.error("LqJdbc",e);
							e.printStackTrace();
						} catch (InvocationTargetException e) {g();
							log.error("LqJdbc",e);
							e.printStackTrace();
						}
					}
        		}
			}
		}
       
        
        if(type==entitySave){
        	// 拼成的字符串最后面多了一个逗号。所以截取除了最后一个字符之外的其他字符
        	str1 = str1.substring(0, str1.length() - 1);
        	str2 = str2.substring(0, str2.length() - 1);
        	//System.out.println(str1 + "  " + str2);
		}else if(type==entityUpdate){
			if (strObjectUpdate==null) {
				try {
					throw new LqJdbcException("没有给实体类"+obj+"中的ID配制注解@Id");
				} catch (LqJdbcException e) {g();
					log.error("LqJdbc",e);
					e.printStackTrace();
				}
				return 0;
			}
			strUpdateSql = strUpdateSql.substring(0, strUpdateSql.length() - 1);
			objList.add(strObjectUpdate);
		}else if(type==entityDelete){
			if (strDelObject==null) {
				try {
					throw new LqJdbcException("没有给实体类"+obj+"中的ID配制注解@Id");
				} catch (LqJdbcException e) {g();
					log.error("LqJdbc",e);
					e.printStackTrace();
				}
				return 0;
			}
			objList.add(strDelObject);
		}
    	int b=1;
    	String sql = null;
		if (type == entitySave) { // save
			sql = "insert into  " + tableName + " (" + str1 + ")   values (" + str2 + ")";
		} else if (type == entityUpdate) { // update
			sql = "update " + tableName + " set "+strUpdateSql+" where "+strUpdateWhereSql; 
		} else if (type == entityDelete) {// delete
			sql = "delete from " + tableName + "  where  "+strDelWhereSql;
		}
		log.info(sql);
    	try {
	        java.sql.PreparedStatement  ps= conn.prepareStatement(sql);  
	        for(int i=0;i<objList.size();i++){
	           ps.setObject(i+1,objList.get(i));
	        }
	        ps.executeUpdate();  
    	}catch(Exception e){
    		g();
    		b=0;
    		try {conn.rollback();} catch (SQLException e1) {
    			log.error("LqJdbc",e1);
    			e1.printStackTrace();
    		}
    		log.error("LqJdbc",e);
			e.printStackTrace();
    	}
    	return b;
    }
    
    //@Deprecated
    protected static <T> T executeResultSet(Class<T> cls, ResultSet rs) throws InstantiationException, IllegalAccessException, SQLException {
        T obj = cls.newInstance();
        ResultSetMetaData rsm = rs.getMetaData();
        int columnCount = rsm.getColumnCount();
        Field[] fields = cls.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);//类中的成员变量为private,故必须进行此操作
            Column zd = field.getAnnotation(Column.class);
            String fieldName=null;
            if (zd!=null) {
            	fieldName = zd.column();
			}else {
				fieldName = field.getName();
			}
            for (int j = 1; j <= columnCount; j++) {
                String columnName = rsm.getColumnName(j);
                if (zd!=null && fieldName.equalsIgnoreCase(columnName)) {//如果注解存在，按注解字段名执行
                    Object value = rs.getObject(j);
                    if (value instanceof BigDecimal) {
                    	//field.set(obj, ((BigDecimal) value).doubleValue());
                    	field.set(obj, value);
					}else {
						field.set(obj, value);
					}
                    break;
                }else if(zd==null) {
                	if(fieldName.equalsIgnoreCase(xhxCap(columnName))) {//如果去下滑线大写存在，按去下滑线大写字段字执行
                		Object value = rs.getObject(j);
                        if (value instanceof BigDecimal) {
                        	//field.set(obj, ((BigDecimal) value).doubleValue());
                        	field.set(obj, value);
    					}else {
    						field.set(obj, value);
    					}
                        break;
                	}else if(fieldName.equalsIgnoreCase(columnName)) {//如果以上都不存在，按属性名等字段名执行
                		Object value = rs.getObject(j);
                        if (value instanceof BigDecimal) {
                        	//field.set(obj, ((BigDecimal) value).doubleValue());
                        	field.set(obj, value);
    					}else {
    						field.set(obj, value);
    					}
                        break;
                	}else {
                		//没有匹配到当前字段，实体类中不存在。
                	}
                }
            }
        }
        return obj;
    }
    
    protected void insertSqlLog(String sql,Object... obj){
    	StringBuffer sqlSB=new StringBuffer("SQL语句:");
		log.info(sqlSB.append(sql.replaceAll("\r\n", " ")));
		StringBuffer csSB=new StringBuffer("SQL参数:");
		for (int i = 0; i < obj.length; i++) {
			csSB.append(obj[i]).append("___");
		}
		log.info(csSB);
    }
    
    protected void insertObjLog(Object... obj){
		StringBuffer csSB=new StringBuffer("SQL参数:");
		for (int i = 0; i < obj.length; i++) {
			csSB.append(obj[i]).append("___");
		}
		log.info(csSB);
    }
    
    
    /**
     * 下划线转去掉,后一个字母转大写
     * @param str
     * @return
     */
    protected static String xhxCap(String str){
    	String[] col=str.split("_");
    	StringBuffer sBuffer=new StringBuffer();
    	for (int j = 0; j < col.length; j++) {
    		if (j==0) {
    			sBuffer.append(col[j]);
			}else {
				sBuffer.append(col[j]);
			}
		}
    	return sBuffer.toString();
    }
	
}
