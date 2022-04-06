package com.brilliance.mysql;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.brilliance.baseConfig;
import com.brilliance.mysql.dao._mysql_base_dao;
import com.brilliance.mysql.dao.mysqlDaoUtil;
import com.brilliance.util.DateTimeUtil;


public class mysqlBase_service<T>{
	protected Logger logger = LoggerFactory.getLogger(mysqlBase_service.class);
	private int NumberPerPage= baseConfig.NumberPerPage;
	@Autowired
	protected _mysql_base_dao<T>  mydao;
	
	public void doBigQuery(final String sql, RowCallbackHandler rc) {
		mydao.doBigQuery(sql, rc);
	}
	/******* 基本操作 *************/
	public int insert(T bean) {
		return mydao.insert(bean);
	}
	public int insert(T bean , String colName) {
		return mydao.insert(bean , colName);
	}
	public int insertAll(List<T> list) {
		return mydao.insertAll(list);
	}

	public int update(T bean) {
		return mydao.save(bean);
	}
	public int update(T bean, String colName) {
		return mydao.save(bean,colName);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public T getById(String id) {	
		ParameterizedType pt = (ParameterizedType)this.getClass().getGenericSuperclass();
		Class cls = (Class) pt.getActualTypeArguments()[0];
		return (T) mydao.getById(id , cls);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public T getById(String id , String collection_name) {	
		ParameterizedType pt = (ParameterizedType)this.getClass().getGenericSuperclass();
		Class cls = (Class) pt.getActualTypeArguments()[0];
		return (T) mydao.getById(id , cls , collection_name);
	}
	@SuppressWarnings("unchecked")
	public T getOne(T bean) {	
		return (T)mydao.findOne(bean);
	}
	@SuppressWarnings("unchecked")
	public T getOne(T bean , String colName) {	
		return (T)mydao.findOne(bean,colName);
	}

	/******* 查询操作 *************/
	public List<T> getList(T bean) {
		return getList(bean , Sort.unsorted());
	}
	public List<T> getList(T bean , String collection_name) {
		return getList(bean , Sort.unsorted() , collection_name);
	}
	public List<T> getList(T bean,Sort sort) {
		return mydao.getList(bean , sort);
	}
	public List<T> getList(T bean,Sort sort,int iMax) {
		return mydao.getList(bean , sort , iMax);
	}
	public List<T> getList(T bean,Sort sort, String collection_name) {
		return mydao.getList(bean , sort, collection_name);
	}
	public List<T> getList(T bean,Sort sort, String collection_name,int iMax) {
		return mydao.getList(bean , sort ,collection_name, iMax);
	}
	public Page<T> getPage(String p, T bean) {
		return mydao.getPage(bean, PageRequest.of(mysqlDaoUtil.Str2Int(p) - 1, NumberPerPage));
	}
	public Page<T> getPage(String p, T bean , int iPerPage) {
		return mydao.getPage(bean, PageRequest.of(mysqlDaoUtil.Str2Int(p) - 1, iPerPage));
	}
	public Page<T> getPage(String p, T bean, String collection_name) {
		return mydao.getPage(bean, PageRequest.of(mysqlDaoUtil.Str2Int(p) - 1, NumberPerPage), collection_name);
	}
	
	public Page<T> getPage(String p, T bean,Sort sort) {
		return mydao.getPage(bean, PageRequest.of(mysqlDaoUtil.Str2Int(p) - 1, NumberPerPage), sort);
	}
	public Page<T> getPage(String p, T bean,Sort sort, int iPerPage) {
		return mydao.getPage(bean, PageRequest.of(mysqlDaoUtil.Str2Int(p) - 1, iPerPage), sort);
	}
	public Page<T> getPage(String p, T bean,Sort sort, String collection_name) {
		return mydao.getPage(bean, PageRequest.of(mysqlDaoUtil.Str2Int(p) - 1, NumberPerPage), sort , collection_name);
	}
	/******* 数据统计操作 *************/
	public long count_num(T bean) {
		return mydao.count_num(bean);
	}
	public long count_num(T bean , String collection_name) {
		return mydao.count_num(bean , collection_name);
	}
	/********* 生成业务流水号   **************/
	private String chr = "abcdefghijklmnopqrstuvwxyz1234567890";
	public String getYwlsh(int iLenth) {
		StringBuffer s = new StringBuffer();
		s.append(DateTimeUtil.now("yyyyMMddHHmmssSSS"));
		// Random random = new Random(36);
		for (int i = 0; i < iLenth - 17; i++) {
			// System.out.println(new Random().nextInt(36));
			s.append(chr.charAt(new Random().nextInt(36)));
		}
		return s.toString();
	}
	
	/********* 生成主键编码 32位   **************/
	public String getRandomBm() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	/********* 根据地址提取排序数字   **************/
    protected Double getNumByStr(String str) {
    	if(null==str) return (double) 0;
    	String[] t = str.split("[\\D]");
    	String NumStr = null;
    	for (String b : t) {
    	    if (null != b && !"".equals(b)) {
    		// 记录数字字符串
    		if (NumStr == null){
    		    NumStr = b;
    		    if(NumStr.length()>4) NumStr=NumStr.substring(0, 4);
    		}else{
    		    if(b.length()>3) b=b.substring(0,3);//只取前三位
    		    while(b.length()<3) b="0"+b;
    		    
    		    NumStr = NumStr + "." + b;
    		    break;//只要一位小数点就够了
    		    }
    	    }
    	}
    	double d;
    	if (null == NumStr)   d = 0;
    	else {
    	    d= Double.parseDouble(NumStr);
    	}
    	return d;
    }

}
