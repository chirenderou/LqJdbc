package test;
import com.lq.util.jdbc.Jdbc;
import com.lq.util.jdbc.LqJdbcFactory;
import com.lq.util.jdbc.LqPiLiang;

public class 批量执行execute {

	public static void main(String[] args) {
		final String sql="insert into test (name)values(?)";
		Jdbc.piliang(sql,new LqPiLiang() {
			public void piliang(LqJdbcFactory jdbc) {
				for (int i = 0; i < 100000; i++) {
					jdbc.executeS(new Object[]{i+""});
				}
			}
		});
	}
	
}
