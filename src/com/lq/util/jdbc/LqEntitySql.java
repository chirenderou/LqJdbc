package com.lq.util.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 多种数据库语句区别实体类<br>
 * @author 吃人的肉
 * QQ:376870344<br>
 * email:liuqingrou@163.com
 */
public class LqEntitySql {
	
	private String sql;//SQL语句
	private List<Object> cs;//对应的问号参数
	private Map<String,String> map;//需要拼接的语句
	
	public LqEntitySql(){
		sql="";
		cs=new ArrayList<Object>();
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public List<Object> getCs() {
		return cs;
	}
	public void setCs(List<Object> cs) {
		this.cs = cs;
	}
	public Object[] getCsObjects(){
		return cs.toArray();
	}
	public Map<String,String> getMap() {
		return map;
	}
	public void setMap(Map<String,String> map) {
		this.map = map;
	}
	
	

}
