package com.brilliance.mysql.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.brilliance.util.DateTimeUtil;

public class mysqlDaoUtil {
	protected static Logger logger = LoggerFactory.getLogger(mysqlDaoUtil.class);
	
	public static <T> void copy_val(ResultSet rs, T zk) {
		if(rs==null) return;		
		Field[] fds = zk.getClass().getDeclaredFields();
		for (Field fd : fds) {
			Object obj = null;
			String fname = fd.getName();
			try {
				obj = PropertyUtils.getPropertyType(zk, fname);
				if (obj.equals(String.class)) {
					PropertyUtils.setProperty(zk, fname, rs.getString(fname));
				} else if (obj.equals(Date.class)) {
					PropertyUtils.setProperty(zk, fname, rs.getTimestamp(fname));
				} else if (obj.equals(Integer.class)) {
					PropertyUtils.setProperty(zk, fname, rs.getInt(fname));
				} else if (obj.equals(Double.class)) {
					PropertyUtils.setProperty(zk, fname, rs.getDouble(fname));
				}
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SQLException e) {
			}
		}
	}

	public static void copy_val(Object source, Object target) {
		Field[] fds = target.getClass().getDeclaredFields();
		for (Field fd : fds) {
			Object obj = null;
			String fname = fd.getName();
			try {
				if (!PropertyUtils.getPropertyType(target, fname).equals(List.class)) {

					obj = PropertyUtils.getProperty(source, fname);
					if (obj != null) {
						if (PropertyUtils.getPropertyType(target, fname)
								.equals(PropertyUtils.getPropertyType(source, fname)))
							PropertyUtils.setProperty(target, fname, obj);
						else if (PropertyUtils.getPropertyType(target, fname).equals(Date.class)
								&& PropertyUtils.getPropertyType(source, fname).equals(String.class)) {
							if(((String) obj).length()==19)
								PropertyUtils.setProperty(target, fname,
									DateTimeUtil.stringToDate((String) obj, "yyyy-MM-dd HH:mm:ss"));
							else if(((String) obj).length()==14)
								PropertyUtils.setProperty(target, fname,
										DateTimeUtil.stringToDate((String) obj, "yyyyMMddHHmmss"));
								
						}
					}else
						PropertyUtils.setProperty(target, fname, null);
						

				}
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				// logger.error(e.getMessage());
				// e.printStackTrace();
			}
		}

	}
	
	/* ##### 分页时字符转数字 ###############*/
	public static int Str2Int(String s) {
		if (s == null)
		    return 1;
		Integer num = null;
		try {
		    num = new Integer(s);
		} catch (NumberFormatException e) {
		    num = 1;
		}
		return num;
	}
	
	
	/*##### 插入转换函数 ############*/
	public static String getInserSql(Object obj , String tabName) {
		StringBuffer bf = new StringBuffer();
		Field[] fds = obj.getClass().getDeclaredFields();
		bf.append("insert into " + tabName + "(");

		for (Field fd : fds) {// insert
			bf.append(fd.getName() + ",");
		}
		bf.deleteCharAt(bf.length() - 1);
		bf.append(") values (");

		for (Field fd : fds) {// values
			bf.append(":" + fd.getName() + ",");
		}
		bf.deleteCharAt(bf.length() - 1);
		bf.append(")");
		return bf.toString();
	}
	/*##### 更新转换函数 ############*/
	public static String getUpdateSql(Object obj, String tabName) {
		return getUpdateSql(obj,tabName,null);
	}
	public static String getUpdateSql(Object obj, String tabName, String modifySql) {
		String colPrime=getPrimeCol(obj.getClass());		
		StringBuffer bf = new StringBuffer();
		Field[] fds = obj.getClass().getDeclaredFields();
		bf.append("update " + tabName + " set ");
		for (Field fd : fds) {// update
			if (!fd.getName().equals(colPrime))
				bf.append(fd.getName() + " = :" + fd.getName() + ",");
		}
		bf.deleteCharAt(bf.length() - 1);
		bf.append(" where ");
		if (null != modifySql)
			bf.append(modifySql + " and " + colPrime + " = :" + colPrime);
		else
			bf.append(colPrime + " = :" + colPrime);
		return bf.toString();
	}
	
	private static String getPrimeCol(Class cls) {
		Field[] fds = cls.getDeclaredFields();
		for (Field fd : fds) {
			if(fd.getAnnotation(Id.class)!=null) return fd.getName();					
		}
		logger.error("class 没有指定@Id注解");
		return null;
	}
	/*##### 查询转换函数 ############*/
	public static String getQueryIdSql(Class cls, String tableName) {
		return "select * from " + tableName + " where " + getPrimeCol(cls) + " =?";
	}
	
	/*##### 查询转换函数 ############*/
	public static String getQuerySql( Object eq , String tableName) {
		return getQuerySql(eq,tableName, Sort.unsorted());
	}
	public static String getQuerySql(Object eq,String tableName, Sort sort) {
		List<String> sqls = new ArrayList<String>();
		sqls.add("select * from " + tableName + " where 1=1");

		Field[] fds = eq.getClass().getDeclaredFields();
		for (Field fd : fds) {
			Object obj = null;
			String fname = fd.getName();
			if (!"serialVersionUID".equals(fname)) {
				try {
					obj = PropertyUtils.getSimpleProperty(eq, fname);
					if (obj != null) {
						if (obj instanceof String) {//字符格式
							if ("null".equals(obj))
								sqls.add("and " + fname + " is null");
							else if ("not null".equals(obj) || "notnull".equals(obj))
								sqls.add("and " + fname + " is not null");
							else if(((String) obj).startsWith("!"))
								sqls.add("and " + fname + "<>:" + fname);
							else if(!"".equals(obj))
								sqls.add("and " + fname + "=:" + fname);
						}else if (obj instanceof Integer ) {
							if((Integer)obj != 0) sqls.add("and " + fname + "=:" + fname);//0可能是默认值,不处理							
						}else if (obj instanceof Long) {
							if((Long)obj != 0) sqls.add("and " + fname + "=:" + fname);//0可能是默认值,不处理							
						}else if(obj instanceof Date && !fname.contains("_end") && !"".equals(obj)) {// 时间处理
							String end_name = fname + "_end";
							Object obj_e = PropertyUtils.getSimpleProperty(eq, end_name);
							if (obj_e != null)// 不存在就是指定日期查询
								sqls.add("and " + fname + "=:" + fname);
							else// 存在就是阶段日期查询
								sqls.add("and " + fname + ">=:" + fname + " and " + fname + "<=:" + end_name);
						}else 
							sqls.add("and " + fname + "=:" + fname);//其他类别
						
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					return "";
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					return "";
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
					return "";
				}
			}
		}
		if(sort!=null) {
			StringBuffer bf = new StringBuffer();
			sort.get().forEachOrdered(new Consumer<Order>() {
				@Override
				public void accept(Order o) {
					bf.append(o.getProperty());
					bf.append(" ");
					bf.append(o.getDirection().name());
					bf.append(",");
				}				
			});
			
//			for(Order o : sort.get().toList()) {
//				bf.append(o.getProperty());
//				bf.append(" ");
//				bf.append(o.getDirection().name());
//				bf.append(",");
//			}
			if(bf.length()>1) bf.deleteCharAt(bf.length() - 1);			
			if(bf.length()>1) sqls.add("order by " + bf.toString());
		}

		return StringUtils.join(sqls, " ");
	}
	
}
