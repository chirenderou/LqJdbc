package com.lq.util.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author 吃人的肉
 * QQ:376870344<br>
 * email:liuqingrou@163.com
 */
public class LqGoInsertAndUpdateAndSelect {
	
	
    private String[] colnames; // 列名数组
    private String[] colTypes; //列名类型数组
    private int[] colSizes; //列名大小数组
	
	//数据库连接
    private String URL ="";
    private String NAME = "";
    private String PASS = "";
    private String DRIVER ="";
	
	public String insertStr="";
	public String insertObj="";
	public String insertObjMap="";
	public String updateStr="";
	public String updateObj="";
	public String updateObjMap="";
    
	protected LqGoInsertAndUpdateAndSelect() {}
	
    /**
     * 生成SQL语句
     * @param tableName 表名
     * @param privateKey 主键或是编号
     */
	protected LqGoInsertAndUpdateAndSelect(String tableName,String url,String driver,String privateKey){
    	URL=url.split("\\*\\*\\*")[0];
   		NAME=url.split("\\*\\*\\*")[1];
   		PASS=url.split("\\*\\*\\*")[2];
		DRIVER=driver;
		m(tableName,privateKey);
    }
	
	/**
	 * 生成SQL语句
	 * @param tableName 表名
	 * @param privateKey 主键或是编号
	 */
	protected LqGoInsertAndUpdateAndSelect(String tableName,String privateKey){
	   	URL=LqDBOperator.url;
	   	NAME=LqDBOperator.username;
	   	PASS=LqDBOperator.pwd;
	   	DRIVER=LqDBOperator.driverClassName;
		m(tableName,privateKey);
	}
	
	private void m(String tableName,String privateKey){
		//创建连接
        Connection con = null;
        //查要生成实体类的表
        String sql = "select * from " + tableName;
        PreparedStatement pStemt = null;
        try {
            try {
                Class.forName(DRIVER);
            } catch (ClassNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            con = DriverManager.getConnection(URL,NAME,PASS);
            pStemt = con.prepareStatement(sql);
            ResultSetMetaData rsmd = pStemt.getMetaData();
            int size = rsmd.getColumnCount();   //统计列
            colnames = new String[size];
            colTypes = new String[size];
            colSizes = new int[size];
            for (int i = 0; i < size; i++) {
                colnames[i] = rsmd.getColumnName(i + 1);
                colTypes[i] = rsmd.getColumnTypeName(i + 1);
                colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
            }
             
            String insert = "insert into "+tableName;
            String update="update "+tableName+" set ";
            String object="Object[] obj=new Object[]{";
            String mapObject="Object[] obj=new Object[]{";
            
            insert+=" (";
            for (int i = 0; i < colnames.length; i++) {
            	if (colnames.length==i+1) {
            		update+=colnames[i].toLowerCase()+"=?";
            		object+=xhxCap(tableName)+".get"+initcap(xhxCap(colnames[i]))+"()";
            		mapObject+=xhxCap(tableName)+".get(\""+colnames[i].toLowerCase()+"\")";
            		insert+=colnames[i].toLowerCase();
				}else{
					object+=xhxCap(tableName)+".get"+initcap(xhxCap(colnames[i]))+"(),";
					mapObject+=xhxCap(tableName)+".get(\""+colnames[i].toLowerCase()+"\"),";
					update+=colnames[i].toLowerCase()+"=?,";
					insert+=colnames[i].toLowerCase()+",";
				}
			}
            insert+=") ";
            insert+="values";
            insert+=" (";
            for (int i = 0; i < colnames.length; i++) {
            	if (colnames.length==i+1) {
            		insert+="?";
				}else{
					insert+="?,";
				}
			}
            insert+=") ";
            update+=" where "+privateKey+"=? ";
            insertStr=insert;
            insertObj=object+"}";
            insertObjMap=mapObject+"}";
            updateStr=update;
            updateObj=object+","+xhxCap(tableName)+".get"+initcap(xhxCap(privateKey))+"()"+"}";
            updateObjMap=mapObject+","+xhxCap(tableName)+".get(\""+privateKey.toLowerCase()+"\")"+"}";
            
            System.out.println(insertStr);
            System.out.println(insertObj);
            System.out.println(insertObjMap);
            System.out.println(updateStr);
            System.out.println(updateObj);
            System.out.println(updateObjMap);
             
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
          try {
              con.close();
          } catch (SQLException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          }
        }
	}
	
	
	
	/**
     * 功能：将输入字符串的首字母改成大写
     * @param str
     * @return
     */
    private String initcap(String str) {
         
        char[] ch = str.toCharArray();
        if(ch[0] >= 'a' && ch[0] <= 'z'){
            ch[0] = (char)(ch[0] - 32);
        }
         
        return new String(ch);
    }
    
    /**
     * 下划线转去掉,后一个字母转大写
     * @param str
     * @return
     */
    private String xhxCap(String str){
    	String[] col=str.split("_");
    	StringBuffer sBuffer=new StringBuffer();
    	for (int j = 0; j < col.length; j++) {
    		if (j==0) {
    			sBuffer.append(col[j]);
			}else {
				sBuffer.append(initcap(col[j]));
			}
		}
    	return sBuffer.toString();
    }

}
