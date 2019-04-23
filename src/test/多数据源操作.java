package test;

import java.util.List;
import java.util.Map;

import com.lq.util.jdbc.Jdbc;
import com.lq.util.jdbc.Page;

public class 多数据源操作 extends BaseDB{
	
	public 多数据源操作(){
		//得到除默认数据源外的所有数据源
		Map map=Jdbc.getDSNames();
		System.out.println(map);
		//读取默认数据源
		List list=Jdbc.find("select * from test");
		//读取第二个数据源
		List list2=Jdbc.getDS(readDB).find("select * from test");
		//读取第三个数据源
		List list3=Jdbc.getDS(writeDB).find("select * from test");
	}
	
	public void test(String db){
		Page p=Jdbc.getDS(db).findPage("select * from test", 1, Page.DEFAULT_PAGE_SIZE, "select count(*) from test", new Object[]{});
		System.out.println(p.getResult());
	}
	
	public static void main(String[] args) {
		new 多数据源操作();
	}
	
}
