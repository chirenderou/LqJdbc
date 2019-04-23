package com.lq.util.jdbc;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * 操作properties<br>
 * @author 吃人的肉
 * QQ:376870344<br>
 * email:liuqingrou@163.com
 */
public class LqConfig implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Logger logger=Logger.getLogger(LqConfig.class);
	
	private Properties props;

	private String propertiesName = "lqjdbc.properties";

	public LqConfig() {
		props = new Properties();
		init();
	}

	protected LqConfig(String properName) {
		props = new Properties();
		init(properName);
	}

	public void init() {
		init(propertiesName);
	}

	
	
	public void init(String properName) {
		this.propertiesName = properName;
		//String filePath = classPath() + propertiesName;
		//String filePath = classPath();
		//logger.info(filePath);
		if (PropertyConfigurator.path!=null) {
			propertiesName=PropertyConfigurator.path;
		}
		InputStream ips = this.getClass().getClassLoader().getResourceAsStream(propertiesName);
		
		//BufferedReader ipss = new BufferedReader(new InputStreamReader(ips));  
		InputStream in = null;
		try {
			in = new BufferedInputStream(ips);
			props.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!props.isEmpty()) {
			Properties nP=new Properties();
			Set<Entry<Object, Object>> s=props.entrySet();
			Iterator<Entry<Object, Object>> it=s.iterator();
			Map<String,Properties> m=new HashMap<String,Properties>();
			while (it.hasNext()) {
				Entry<Object, Object> en=it.next();
				String key=en.getKey().toString().toLowerCase();
				nP.put(key, en.getValue());
				if (key.indexOf("lqjdbc.appender.")!=-1) {
					String teString=key.substring(0, key.indexOf(".", 16));
					if (m.get(teString.substring(16))==null) {
						Properties m2=new Properties();
						m2.put(key.substring(key.indexOf(".", 16)+1, key.length()), en.getValue());
						m.put(teString.substring(16), m2);
					}else{
						Properties m2=(Properties) m.get(teString.substring(16));
						m2.put(key.substring(key.indexOf(".", 16)+1, key.length()), en.getValue());
					}
				}
			}
			LqDBOperator.dataSourceNameMap=m;
			/*Set set = m.entrySet();
			Iterator<Entry<Object, Object>> it2=set.iterator();
			while (it2.hasNext()) {
				Entry<Object, Object> en=it2.next();
				System.out.println(en.getValue());
			}*/
			props.clear();
			props=nP;
		}
	}

	public String classPath() {
		//String basepath = Thread.currentThread().getContextClassLoader().getResource("").toString();
		//String basepath = System.getProperty("user.dir");
		URL url = ClassLoader.getSystemResource(propertiesName);
		String basepath =url.getPath();
		// System.out.println("---classpath----"+basepath.substring(6,basepath.length()));
		/*if (System.getProperty("file.separator").equals("\\")) {
			return basepath.substring(6, basepath.length());
		} else {
			return basepath.substring(5, basepath.length());
		}*/
		//return basepath+System.getProperty("file.separator");
		basepath=this.getClass().getClassLoader().getResource(propertiesName).getPath();  
		return basepath;

	}
	

	public String getString(String key) {
		try {
			String value = "";
			if (props.getProperty(key.toLowerCase()) != null) {
				value = new String(props.getProperty(key.toLowerCase())
						.getBytes("ISO-8859-1"), "utf-8");
				;
			}
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public Set<String> getPropNames() {

		Set<String> propNameSet = props.stringPropertyNames();

		return propNameSet;
	}

	public void setString(String key, String value) {
		if (!key.equals("")) {
			props.setProperty(key, value);
		}
	}

	public void setString(Map<String, String> map) {
		Set<Entry<String, String>> entrySet = null;
		if (map != null && map.size() > 0) {
			entrySet = map.entrySet();
			Iterator<Entry<String, String>> iterator = entrySet.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				String key = entry.getKey();
				String value = entry.getValue();
				props.setProperty(key, value);
			}
		}

	}

	
	
	
	public void saveConfig(String comments) {
		String filePath = classPath() + propertiesName;
		OutputStream out = null;

		try {
			out = new FileOutputStream(filePath);
			props.store(out, comments);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

//	private static void main(String[] args) {
//		System.out.println(LqConfig.class.getResource("/").getPath());
//		System.out.println(System.getProperty("file.separator"));
//
//	}

}
