package test;

import java.util.Date;
import java.sql.*;
import com.lq.util.jdbc.Id;
import com.lq.util.jdbc.Sql;
import com.lq.util.jdbc.Table;
/**
 * lqtest 实体类
 * Fri May 26 14:49:59 CST 2017  吃人的肉
 */ 
@Table(name="lqtest")
public class Lqtest{
	private Integer id;
	private String mc;
	private Integer nl;
	private Date date;
	private Double wd;
	private Float sd;
	private String zm;
	private boolean b;

	public void setId(Integer id){
		this.id=id;
	}
	@Id
	@Sql(column="id")
	public Integer getId(){
		return id;
	}
	public void setMc(String mc){
		this.mc=mc;
	}
	@Sql(column="mc")
	public String getMc(){
		return mc;
	}
	public void setNl(Integer nl){
		this.nl=nl;
	}
	@Sql(column="nl")
	public Integer getNl(){
		return nl;
	}
	public void setDate(Date date){
		this.date=date;
	}
	@Sql(column="date")
	public Date getDate(){
		return date;
	}
	public void setWd(Double wd){
		this.wd=wd;
	}
	@Sql(column="wd")
	public Double getWd(){
		return wd;
	}
	public void setSd(Float sd){
		this.sd=sd;
	}
	@Sql(column="sd")
	public Float getSd(){
		return sd;
	}
	public void setZm(String zm){
		this.zm=zm;
	}
	@Sql(column="zm")
	public String getZm(){
		return zm;
	}
	public void setB(boolean b){
		this.b=b;
	}
	@Sql(column="b")
	public boolean getB(){
		return b;
	}
}

