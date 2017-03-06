package test;
import java.util.List;

import com.lq.util.jdbc.Jdbc;
import com.lq.util.jdbc.LqJdbcFactory;
import com.lq.util.jdbc.LqShiWu;

public class 并发下操作余额 {

	public static void main(String[] args) throws Exception {
		
		int i=Jdbc.shiwu(new LqShiWu() {
			
			@Override
			public void shiwu(LqJdbcFactory jdbc) {
				
				List list=jdbc.find("select * from admin where id=1 for update", new Object[]{});
				//判断余额是否够
				jdbc.shiB=999;
				
				int i=jdbc.execute("update admin set phone='5' where id=1", new Object[]{});
				System.out.println(i);
				
			}
		});
		if (i==999) {
			System.out.println("金额不足");
		}
	}
	
}
