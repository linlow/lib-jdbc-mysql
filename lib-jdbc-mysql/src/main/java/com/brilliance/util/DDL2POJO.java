package com.brilliance.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

public class DDL2POJO  {
	
	public static void main(String[] args) throws SQLException {
		DDL2POJO d = new DDL2POJO();
		d.tableToBean(conn, "T_CHUZUFANGWU");
	}
	
	String packages = "com.user.table";
	String endPath = System.getProperty("user.dir")
			+ "\\src\\main\\java\\" + (packages.replace("/", "\\")).replace(".", "\\");
	
	private static String user = "syrk";
	private static String password = "bjrk@2019";
	private static String url = "jdbc:oracle:thin:@10.9.28.143:1521:ORCL";
	private static Connection conn = null;
	private static final String LINE = "\r\n";
	private static final String TAB = "\t";
	private static Map<String, String> map;

	static {
		map = new HashMap<String, String>();
		map.put("VARCHAR", "String");
		map.put("VARCHAR2", "String");
		map.put("CLOB", "String");
		map.put("NVARCHAR", "String");
		map.put("INTEGER", "int");
		map.put("NUMBER", "Integer");
		map.put("FLOAT", "float");
		map.put("TIMESTAMP", "Timestamp");
		map.put("CHAR", "String");
		map.put("DATETIME", "Date");
		map.put("DATE", "Date");
		map.put("TIMESTAMP_IMPORT", "import java.util.Date");
		map.put("DATETIME_IMPORT", "import java.util.Date");
		map.put("DATE_IMPORT", "import java.util.Date");

		Properties p = new Properties();
		p.put("user", user);
		p.put("password", password);
		p.put("remarksReporting", "true");
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			conn = DriverManager.getConnection(url, p);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void tableToBean(Connection connection, String tableName) throws SQLException {
		ResultSet resultSet = conn.getMetaData().getColumns(null, user.toUpperCase(), tableName.toUpperCase(), "%");
		StringBuffer sb = new StringBuffer();		
		tableName = tableName.toLowerCase();
		sb.append("package " + this.packages + " ;");
		sb.append(LINE);
		importPackage(resultSet, sb);
		resultSet.close();
		resultSet = conn.getMetaData().getColumns(null, user.toUpperCase(), tableName.toUpperCase(), "%");
		sb.append(LINE);
		sb.append(LINE);
		sb.append("public class " + tableName + " {");
		sb.append(LINE);
		defProperty(resultSet, sb);
		resultSet.close();
		resultSet = conn.getMetaData().getColumns(null, user.toUpperCase(), tableName.toUpperCase(), "%");
		genSetGet(resultSet, sb);
		sb.append("}");		
		System.out.println("文件在目录" + endPath + "下");
		buildJavaFile(endPath + "\\" + tableName + ".java", sb.toString());
	}

	public static String getPojoType(String dataType) {
		StringTokenizer st = new StringTokenizer(dataType);
		return map.get(st.nextToken());
	}

	public static String getImport(String dataType) {
		if (map.get(dataType) == null || "".equals(map.get(dataType))) {
			return null;
		} else {
			return map.get(dataType);
		}
	}

	// 导入属性所需包
	private void importPackage(ResultSet rs, StringBuffer sb) throws SQLException {
		while (rs.next()) {
			String im = getImport(rs.getString("TYPE_NAME") + "_IMPORT");
			if (im != null) {
				sb.append(im + ";");
				sb.append(LINE);
				map.remove(rs.getString("TYPE_NAME") + "_IMPORT");
			}
		}
	}

	// 属性定义
	private void defProperty(ResultSet rs, StringBuffer sb) throws SQLException {

		while (rs.next()) { 
			sb.append(TAB);
			String columnName = rs.getString("COLUMN_NAME").toLowerCase();
			sb.append("private " + getPojoType(rs.getString("TYPE_NAME")) + " " + columnName + ";");
			if(rs.getString("REMARKS")!=null) sb.append("//"+rs.getString("REMARKS"));
			sb.append(LINE);
			
			System.out.println(rs.getString("TYPE_NAME"));
		}
	}

	// 属性生成get、 set 方法
	private void genSetGet(ResultSet rs, StringBuffer sb) throws SQLException {
		while (rs.next()) {
			sb.append(TAB);
			String pojoType = getPojoType(rs.getString("TYPE_NAME"));
			String columnName = rs.getString("COLUMN_NAME").toLowerCase();
			String getName = null;
			String setName = null;
			if (columnName.length() > 1) {
				getName = "public " + pojoType + " get" + columnName.substring(0, 1).toUpperCase()
						+ columnName.substring(1, columnName.length()) + "() {";
				setName = "public void set" + columnName.substring(0, 1).toUpperCase()
						+ columnName.substring(1, columnName.length()) + "(" + pojoType + " " + columnName + ") {";
			} else {
				getName = "public get" + columnName.toUpperCase() + "() {";
				setName = "public set" + columnName.toUpperCase() + "(" + pojoType + " " + columnName + ") {";
			}
			sb.append(LINE).append(TAB).append(getName);
			sb.append(LINE).append(TAB).append(TAB);
			sb.append("return " + columnName + ";");
			sb.append(LINE).append(TAB).append("}");
			sb.append(LINE);
			sb.append(LINE).append(TAB).append(setName);
			sb.append(LINE).append(TAB).append(TAB);
			sb.append("this." + columnName + " = " + columnName + ";");
			sb.append(LINE).append(TAB).append("}");
			sb.append(LINE);

		}
	}

	// 生成java文件
	public void buildJavaFile(String filePath, String fileContent) {
		try {
			File file = new File(filePath);
			FileOutputStream osw = new FileOutputStream(file);
			PrintWriter pw = new PrintWriter(osw);
			pw.println(fileContent);
			pw.close();
		} catch (Exception e) {
			System.out.println("生成文件出错：" + e.getMessage());
		}
	}
}
