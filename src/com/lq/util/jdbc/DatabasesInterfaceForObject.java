package com.lq.util.jdbc;

/**
 * 数据库语句分类<br>
 * @author 吃人的肉
 * QQ:376870344<br>
 * email:liuqingrou@163.com
 */
public interface DatabasesInterfaceForObject {

	public Object oracle();
	public Object mysql();
	public Object sqlserver();
	public String sqlLite();
	
}
