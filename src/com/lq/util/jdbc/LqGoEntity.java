package com.lq.util.jdbc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
 
/**
 * @author 吃人的肉
 * QQ:376870344<br>
 * email:liuqingrou@163.com
 */
public class LqGoEntity {
     
	private String authorName = "吃人的肉";//作者名字  
	private String bao="com.lq.entity";//指定实体生成所在包的路径  
    private String tablename = "dls_level";//表名
    private String[] colnames; // 列名数组
    private String[] colTypes; //列名类型数组
    private String[] colshowNames;//显示列名数组
    private int[] colSizes; //列名大小数组
    private boolean f_util = false; // 是否需要导入包java.util.*
    private boolean f_sql = false; // 是否需要导入包java.sql.*
     
    //数据库连接
    private static String URL ="";
    private static String NAME = "";
    private static String PASS = "";
    private static String DRIVER ="com.mysql.jdbc.Driver";
    private static String primaryId="";
    
    protected LqGoEntity() {}
     
     /**
      * 生成表对应实体类
      * @param path 实体类生成在哪个包下面   如:路径 com.lq.entity
      * @param tableName 表名
      * @param url 连接
      * @param driver 驱动名
      * @param privateKey 主键,无主键设成唯一标识符的字段
      */
    protected LqGoEntity(String path,String tableName,String url,String driver,String privateKey){
    	 bao=path;
    	 tablename=tableName;
    	 URL=url.split("\\*\\*\\*")[0];
    	 NAME=url.split("\\*\\*\\*")[1];
    	 PASS=url.split("\\*\\*\\*")[2];
    	 DRIVER=driver;
    	 m(privateKey);
     }
     
     /**
      * 生成表对应实体类
      * @param path 实体类生成在哪个包下面   如:路径 com.lq.entity
      * @param tableName 表名
      * @param privateKey 主键,无主键设成唯一标识符的字段
      */
    protected LqGoEntity(String path,String tableName,String privateKey){
    	bao=path;
   	 	tablename=tableName;
		URL=LqDBOperator.url;
	   	NAME=LqDBOperator.username;
	   	PASS=LqDBOperator.pwd;
	   	DRIVER=LqDBOperator.driverClassName;
        m(privateKey);
    }
    
    private void m(String privateKey){
    	//创建连接
        Connection con=null;
        //查要生成实体类的表
        String sql = "select *,count(*) from " + tablename;
        if(LqDBOperator.driverClassName.toLowerCase().indexOf("sqlserver")!=-1){
        	sql="SELECT TOP 1 * FROM ( select ROW_NUMBER() OVER (ORDER BY "+privateKey+") AS RowNumber,* from "+tablename+" ) as row_ WHERE RowNumber > "+1*(1-1);
        }
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
            pStemt.executeQuery();
            ResultSetMetaData rsmd = pStemt.getMetaData();
            DatabaseMetaData dbMeta = con.getMetaData(); 
            
            System.out.println("数据库已知的用户: "+ dbMeta.getUserName());   
            System.out.println("数据库的系统函数的逗号分隔列表: "+ dbMeta.getSystemFunctions());   
            System.out.println("数据库的时间和日期函数的逗号分隔列表: "+ dbMeta.getTimeDateFunctions());   
            System.out.println("数据库的字符串函数的逗号分隔列表: "+ dbMeta.getStringFunctions());   
            System.out.println("数据库供应商用于 'schema' 的首选术语: "+ dbMeta.getSchemaTerm());   
            System.out.println("数据库URL: " + dbMeta.getURL());   
            System.out.println("是否允许只读:" + dbMeta.isReadOnly());   
            System.out.println("数据库的产品名称:" + dbMeta.getDatabaseProductName());   
            System.out.println("数据库的版本:" + dbMeta.getDatabaseProductVersion());   
            System.out.println("驱动程序的名称:" + dbMeta.getDriverName());   
            System.out.println("驱动程序的版本:" + dbMeta.getDriverVersion());  
            
            System.out.println("数据库中使用的表类型");   
            ResultSet rs = dbMeta.getTableTypes();   
            while (rs.next()) {   
                System.out.println(rs.getString(1));   
            }   
            rs.close();   
            
            ResultSet pkRSet = dbMeta.getPrimaryKeys(null, null, tablename);
            while( pkRSet.next() ) { 
            	System.err.println("****** Comment ******"); 
            	System.err.println("TABLE_CAT : "+pkRSet.getObject(1)); 
            	System.err.println("TABLE_SCHEM: "+pkRSet.getObject(2)); 
            	System.err.println("TABLE_NAME : "+pkRSet.getObject(3)); 
            	System.err.println("COLUMN_NAME: "+pkRSet.getObject(4)); 
            	System.err.println("KEY_SEQ : "+pkRSet.getObject(5)); 
            	System.err.println("PK_NAME : "+pkRSet.getObject(6)); 
            	System.err.println("****** ******* ******"); 
            	primaryId=(String) pkRSet.getObject(4);
            }
            pkRSet.close();
            int size = rsmd.getColumnCount();   //统计列
            colnames = new String[size-1];
            colTypes = new String[size-1];
            colSizes = new int[size-1];
            colshowNames=new String[size-1];
            int c=0;
            for (int i = 0; i < size; i++) {
            	if (rsmd.getColumnName(i+1).equalsIgnoreCase("count(*)")||rsmd.getColumnName(i+1).equalsIgnoreCase("RowNumber")) {
					continue;
				}
                colnames[c] = rsmd.getColumnName(i + 1);
                colTypes[c] = rsmd.getColumnTypeName(i + 1);
                
                String colNam=colnames[c];
            	colshowNames[c]=xhxCap(colNam);
            	
                if(colTypes[c].equalsIgnoreCase("datetime")||colTypes[c].equalsIgnoreCase("date") || colTypes[c].equalsIgnoreCase("timestamp")){
                    f_util = true;
                }
                if(colTypes[c].equalsIgnoreCase("image") || colTypes[c].equalsIgnoreCase("text")||colTypes[c].equalsIgnoreCase("blob") || colTypes[c].equalsIgnoreCase("char")){
                    f_sql = true;
                }
                colSizes[c] = rsmd.getColumnDisplaySize(i + 1);
                c+=1;
            }
             
            String content = parse(colnames,colTypes,colSizes);
             
            try {
                File directory = new File("");
                //System.out.println("绝对路径："+directory.getAbsolutePath());
                //System.out.println("相对路径："+directory.getCanonicalPath());
                //String path=this.getClass().getResource("").getPath();
                File f=new File(directory.getAbsolutePath()+ "/src/"+bao.replaceAll("\\.", "/")+"/");
                if (!f.exists()) {
        			f.mkdirs();
        		}
                String wPath=directory.getAbsolutePath()+ "/src/"+bao.replaceAll("\\.", "/")+"/" + xhxCap(initcap(tablename)) + ".java";
                FileWriter fw = new FileWriter(wPath);
                PrintWriter pw = new PrintWriter(fw);
                pw.println(content);
                pw.flush();
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
             
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
     * 功能：生成实体类主体代码
     * @param colnames
     * @param colTypes
     * @param colSizes
     * @return
     */
    private String parse(String[] colnames, String[] colTypes, int[] colSizes) {
        StringBuffer sb = new StringBuffer();
        
        sb.append("package "+bao+";\r\n");
        sb.append("\r\n");
         
        //判断是否导入工具包
        if(f_util){
            sb.append("import java.util.Date;\r\n");
        }
        if(f_sql){
            sb.append("import java.sql.*;\r\n");
        }
        sb.append("import com.lq.util.jdbc.Id;\r\n")
        .append("import com.lq.util.jdbc.Sql;\r\n")
        .append("import com.lq.util.jdbc.Table;\r\n");
        //注释部分
        sb.append("/**\r\n");
        sb.append(" * "+tablename+" 实体类\r\n");
        sb.append(" * "+new Date()+"  "+authorName+"\r\n");
        sb.append(" */ \r\n");
        //实体部分
        sb.append("@Table(name=\""+tablename+"\")\r\n");
        sb.append("public class " + xhxCap(initcap(tablename)) + "{\r\n");
        processAllAttrs(sb);//属性
        sb.append("\r\n");
        processAllMethod(sb);//get set方法
        sb.append("}\r\n");
         
        //System.out.println(sb.toString());
        return sb.toString();
    }
     
    /**
     * 功能：生成所有属性
     * @param sb
     */
    private void processAllAttrs(StringBuffer sb) {
         
        for (int i = 0; i < colnames.length; i++) {
            sb.append("\tprivate " + sqlType2JavaType(colTypes[i].toLowerCase()) + " " + colshowNames[i] + ";\r\n");
        }
         
    }
 
    /**
     * 功能：生成所有方法
     * @param sb
     */
    private void processAllMethod(StringBuffer sb) {
         
        for (int i = 0; i < colnames.length; i++) {
            sb.append("\tpublic void set" + initcap(colshowNames[i]) + "(" + sqlType2JavaType(colTypes[i].toLowerCase()) + " " + 
            		colshowNames[i] + "){\r\n");
            sb.append("\t\tthis." + colshowNames[i] + "=" + colshowNames[i] + ";\r\n");
            sb.append("\t}\r\n");
            if (colnames[i].toLowerCase().equals(primaryId.toLowerCase())) {
            	sb.append("\t@Id\r\n");
			}
            sb.append("\t@Sql(column=\""+colnames[i]+"\")\r\n");
            sb.append("\tpublic " + sqlType2JavaType(colTypes[i].toLowerCase()) + " get" + initcap(colshowNames[i]) + "(){\r\n");
            sb.append("\t\treturn " + colshowNames[i] + ";\r\n");
            sb.append("\t}\r\n");
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
 
    /**
     * 功能：获得列的数据类型
     * @param sqlType
     * @return
     */
    private String sqlType2JavaType(String sqlType) {
         
        if(sqlType.equalsIgnoreCase("bit")){
            return "boolean";
        }else if(
        		   sqlType.equalsIgnoreCase("blob")
        ){
            return "Byte";
        }else if(sqlType.equalsIgnoreCase("smallint")){
            return "Short";
        }else if(
        		   sqlType.equalsIgnoreCase("tinyint")
        		|| sqlType.equalsIgnoreCase("int")
        		|| sqlType.equalsIgnoreCase("integer")
        		|| sqlType.indexOf("identity")!=-1
        ){
            return "Integer";
        }else if(
        		   sqlType.equalsIgnoreCase("bigint")
        		|| sqlType.equalsIgnoreCase("number")
        ){
            return "Long";
        }else if(
        		   sqlType.equalsIgnoreCase("float")
        		|| sqlType.equalsIgnoreCase("binary_float")
        ){
            return "Float";
        }else if(
        		   sqlType.equalsIgnoreCase("decimal") 
        		|| sqlType.equalsIgnoreCase("numeric") 
                || sqlType.equalsIgnoreCase("real") 
                || sqlType.equalsIgnoreCase("money") 
                || sqlType.equalsIgnoreCase("smallmoney")
                || sqlType.equalsIgnoreCase("binary_double")
				|| sqlType.equalsIgnoreCase("double")
        ){
            return "Double";
        }else if(
        		   sqlType.equalsIgnoreCase("varchar") 
        		|| sqlType.equalsIgnoreCase("char") 
        		|| sqlType.equalsIgnoreCase("nvarchar2")  
                || sqlType.equalsIgnoreCase("nvarchar") 
                || sqlType.equalsIgnoreCase("nchar") 
                || sqlType.equalsIgnoreCase("varchar2")
                || sqlType.equalsIgnoreCase("text")){
            return "String";
        }else if(
        		   sqlType.equalsIgnoreCase("datetime")
        		|| sqlType.equalsIgnoreCase("date") 
        		|| sqlType.equalsIgnoreCase("timestamp")  
                || sqlType.equalsIgnoreCase("timestamp with local time zone")   
                || sqlType.equalsIgnoreCase("timestamp with time zone")
        ){
            return "Date";
        }else if(sqlType.equalsIgnoreCase("image")){
            return "Blod";
        }else{
			System.out.println(sqlType);
		}
         
        return null;
    }
    
    
 
}
