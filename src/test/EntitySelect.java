package test;

import com.lq.util.jdbc.Column;

public class EntitySelect {

	@Column(column="ddd")
	private String aaa;
	
	@Column(column="fff")
	private String bbb;

	public String getAaa() {
		return aaa;
	}
	public void setAaa(String aaa) {
		this.aaa = aaa;
	}
	public String getBbb() {
		return bbb;
	}
	public void setBbb(String bbb) {
		this.bbb = bbb;
	}
	
	
	
}
