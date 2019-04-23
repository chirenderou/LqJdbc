package test;

import java.util.Date;
import java.sql.*;
import com.lq.util.jdbc.Id;
import com.lq.util.jdbc.Column;
import com.lq.util.jdbc.Sql;
import java.math.*;
import com.lq.util.jdbc.Table;
/**
 * test2 实体类
 * Thu Jun 08 09:15:49 CST 2017  吃人的肉
 */ 
@Table(name="test2")
public class Test2{
	@Column(column="uk_id")
	private BigInteger ukId;
	@Column(column="idx_name")
	private String idxName;
	@Column(column="remark")
	private String remark;
	@Column(column="weight")
	private BigDecimal weight;
	@Column(column="gmt_create")
	private Date gmtCreate;
	@Column(column="gmt_modified")
	private Date gmtModified;

	public void setUkId(BigInteger ukId){
		this.ukId=ukId;
	}
	@Id
	@Sql(column="uk_id")
	public BigInteger getUkId(){
		return ukId;
	}
	public void setIdxName(String idxName){
		this.idxName=idxName;
	}
	@Sql(column="idx_name")
	public String getIdxName(){
		return idxName;
	}
	public void setRemark(String remark){
		this.remark=remark;
	}
	@Sql(column="remark")
	public String getRemark(){
		return remark;
	}
	public void setWeight(BigDecimal weight){
		this.weight=weight;
	}
	@Sql(column="weight")
	public BigDecimal getWeight(){
		return weight;
	}
	public void setGmtCreate(Date gmtCreate){
		this.gmtCreate=gmtCreate;
	}
	@Sql(column="gmt_create")
	public Date getGmtCreate(){
		return gmtCreate;
	}
	public void setGmtModified(Date gmtModified){
		this.gmtModified=gmtModified;
	}
	@Sql(column="gmt_modified")
	public Date getGmtModified(){
		return gmtModified;
	}
}

