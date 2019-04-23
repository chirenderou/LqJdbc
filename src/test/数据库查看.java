package test;

import java.sql.Connection;
import java.util.Map;

import com.lq.util.jdbc.Jdbc;

public class 数据库查看 {

	public static void main(String[] args) {
		Connection con=Jdbc.getCon();//得到一个连接
		System.out.println(con);
		Map<String, String> map=Jdbc.getDSNames();//得到所有数据源名称
		System.out.println(map);
		System.out.println(Jdbc.getNumBusyConnectionsDefaultUser());
		System.out.println(Jdbc.getNumConnectionsDefaultUser());
		System.out.println(Jdbc.getNumIdleConnectionsDefaultUser());
	}
	
}
