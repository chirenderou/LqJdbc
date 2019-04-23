package test;

import java.util.List;

import org.apache.commons.collections.map.ListOrderedMap;

import com.lq.util.jdbc.Jdbc;
import com.lq.util.jdbc.Page;

public class 查询实例 {

	public static void main(String[] args) {
		String sql="select '张三' name,18 age,'哈哈' a_b from dual";
		String sqlCount="select count(*) c from dual";
		Page<ListOrderedMap> p=Jdbc.findPage(sql,1,15,Long.valueOf("1"));//分页Map不安全查询
		Page<Student> p2=Jdbc.findPage(sql,1,15,Long.valueOf("1"),Student.class);//分页实体类不安全查询
		Page<ListOrderedMap> p3=Jdbc.findPage(sql,1,15,Long.valueOf("1"),new Object[] {});//分页Map安全查询
		Page<ListOrderedMap> p4=Jdbc.findPage(sql,1,15,sqlCount,new Object[] {});//分页Map并传入总数sql安全查询
		Page<Student> p5=Jdbc.findPage(sql,1,15,Long.valueOf("1"),Student.class,new Object[] {});//分页实体类安全查询
		Page<Student> p6=Jdbc.findPage(sql,1,15,sqlCount,Student.class,new Object[] {});//分页实体类并传入总数sql安全查询
		List<ListOrderedMap> list=Jdbc.find(sql);//Map不安全查询
		List<Student> list2=Jdbc.find(sql,Student.class);//实体类不安全查询
		List<ListOrderedMap> list3=Jdbc.find(sql,new Object[] {});//Map安全查询
		List<Student> list4=Jdbc.find(sql,Student.class,new Object[] {});//实体类安全查询
		System.out.println(p.getData());
		System.out.println(p2.getData());System.out.println(p2.getData().get(0).getaB());
		System.out.println(p3.getData());
		System.out.println(p4.getData());
		System.out.println(p5.getData());
		System.out.println(p6.getData());
		System.out.println(list);
		System.out.println(list2);
		System.out.println(list3);
		System.out.println(list4);
	}
	
}
