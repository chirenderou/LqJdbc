package test;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.lq.util.jdbc.DatabasesInterface;
import com.lq.util.jdbc.Jdbc;
import com.lq.util.jdbc.LqEntitySql;
import com.lq.util.jdbc.LqJdbcFactory;
import com.lq.util.jdbc.LqPiLiang;
import com.lq.util.jdbc.LqResultSet;
import com.lq.util.jdbc.LqShiWu;
import com.lq.util.jdbc.Page;


public class Test extends BaseDB{
	
	public static void main(String[] args) {
		
	}
	
	public void pageSelectTest(){
		//SQL SERVER 2005 分页查询用法
		String sql="SELECT ROW_NUMBER() OVER (ORDER BY id) AS RowNumber,* from payinfo";
		String sqlCount="select count(*) from payinfo";
		//其它数据库SQL分页
		sql="SELECT * from payinfo";
		sqlCount="select count(*) from payinfo";
		
		Page page=Jdbc.findPage(sql, 1, 3, sqlCount, new Object[]{});
		System.out.println(page.getData().size());
	}
	
	public void shuoM(){
		/**
		 * 生成实体类
		 */
		Jdbc.createEntity("com.entity","t_user","id");
		/**
		 * 生成插入修改SQL语句
		 */
		Jdbc.createSQL("t_user","id");
		
		//-------------------------下面为SQL语句操作----------------------------------------
		
		/**
		 * 查询
		 */
		Jdbc.find("select * from t_user where id=?", new Object[]{"1"});
		/**
		 * 分页查询
		 */
		Page page=Jdbc.findPage("select * from t_user where id=?", 1, 15, "select count(id) from t_user where id=?", new Object[]{"1"});
		/**
		 * 插入
		 */
		Jdbc.execute("insert into test (name)values(?)", new Object[]{"吃人的肉"});
		/**
		 * 修改
		 */
		Jdbc.execute("update test set name=? where id=?", new Object[]{"吃人的肉1","1"});
		/**
		 * 删除
		 */
		Jdbc.execute("delete from test where id=?",new Object[]{"1"});
		
		//------------------------下面为实体类操作------------------------------------------
		
		final Test test=new Test();
		Jdbc.save(test);//插入
		Jdbc.update(test);//修改
		Jdbc.delete(test);//删除
		List<Test> list=Jdbc.find("select * from t_user", Test.class, new Object[]{});//查询返回实体类
		
		//------------------------下面为事务操作------------------------------------------
		
		/**
		 * 事务处理
		 */
		Jdbc.shiwu(new LqShiWu() {
			@Override
			public void shiwu(LqJdbcFactory jdbc) {
				if (1!=1) {//验证判断
					jdbc.shiB=9;//如果验证通过返回自定义的状态码
					return;
				}
				jdbc.find("select * from t_user where id=?", new Object[]{"1"});
				jdbc.execute("insert into test (name)values(?)", new Object[]{"吃人的肉"});
				jdbc.execute("update test set name=? where id=?", new Object[]{"吃人的肉1","1"});
				jdbc.execute("delete from test where id=?",new Object[]{"1"});
				Jdbc.save(test);//插入
				Jdbc.update(test);//修改
				Jdbc.delete(test);//删除
			}
		});
		
		/**
		 * 操作结果集
		 */
		Jdbc.operationResultSet(new LqResultSet() {
			@Override
			public void getResultSet(LqJdbcFactory jdbc) {
				ResultSet rs = jdbc.findResultSet("select photo from t_user where id=?",new Object[]{"5"});
				try {
					while (rs.next()) {
						InputStream in=rs.getBinaryStream("photo");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		
		/**
		 * 多种数据库操作
		 */
		final StringBuffer sb=new StringBuffer();
		final List listCs2=new ArrayList();
		Jdbc.sql(new DatabasesInterface() {
			public String oracle() {
				sb.append("select * from admin where id=?");
				listCs2.add("1");
				return null;
			}
			public String mysql() {
				sb.append("select * from admin where id=?");
				listCs2.add("1");
				return null;
			}
			public String sqlserver() {
				sb.append("select * from admin where id=?");
				listCs2.add("1");
				return null;
			}
			public String sqlLite() {
				sb.append("select * from admin where id=?");
				listCs2.add("1");
				return null;
			}
		});
		List list2=Jdbc.find(sb.toString(),listCs2.toArray());
		System.out.println(list2);
		
		
		/**
		 * 批量执行
		 */
		final String sql="insert into test (name)values(?)";
		Jdbc.piliang(sql,new LqPiLiang() {
			public void piliang(LqJdbcFactory jdbc) {
				for (int i = 0; i < 100; i++) {
					jdbc.executeS(new Object[]{i+""});
				}
			}
		});
		
		/**
		 * 多数据源操作
		 */
		List listDdb=Jdbc.find("select * from test");//读取默认数据源
		List listDrdb=Jdbc.getDS(readDB).find("select * from test");//读取第二个数据源
		List listDwdb=Jdbc.getDS(writeDB).find("select * from test");//读取第三个数据源
		
	}
}
