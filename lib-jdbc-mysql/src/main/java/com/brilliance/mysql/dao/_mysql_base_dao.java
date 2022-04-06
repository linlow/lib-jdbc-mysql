package com.brilliance.mysql.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.RowCallbackHandler;

public interface _mysql_base_dao<T> {
	//计数统计
	long count_num(T bean);
	long count_num(T bean, String tableName);
		
	@SuppressWarnings("hiding")
	public <T> List<T> getList(T bean);//读取全部数据
	@SuppressWarnings("hiding")
	public <T> List<T> getList(T bean, String tableName);
	@SuppressWarnings("hiding")
	public <T> List<T> getList(T bean , Sort sort);//读取全部数据,指定排序
	@SuppressWarnings("hiding")
	public <T> List<T> getList(T bean , Sort sort , int iMax);//读取前几条数据
	@SuppressWarnings("hiding")
	public <T> List<T> getList(T bean, Sort sort, String tableName);
	@SuppressWarnings("hiding")
	public <T> List<T> getList(T bean , Sort sort, String tableName, int iMax);//读取前几条数据
	@SuppressWarnings("hiding")
	public <T> Page<T> getPage(T bean, Pageable pageable);//分页读取##注意:页数从 0 开始
	@SuppressWarnings("hiding")
	public <T> Page<T> getPage(T bean, Pageable pageable, String tableName);//分页读取##注意:页数从 0 开始
	@SuppressWarnings("hiding")
	public <T> Page<T> getPage(T bean, Pageable pageable ,Sort sort);//分页读取##注意:页数从 0 开始
	@SuppressWarnings("hiding")
	public <T> Page<T> getPage(T bean, Pageable pageable, Sort sort, String tableName);
	

	public void doBigQuery(final String sql, RowCallbackHandler rc);
	//分组统计
	//public List<result_groupby_data> getGroupBy(String tj_ziduan , T bean);//
	//public List<result_groupby_data> getGroupBy(String dwmc , String tj_ziduan , T bean);

	//保存
	public int insert(T bean);
	public int insert(T bean, String tableName);
	public int insertAll(List<T> list);
	public int save(T bean);
	public int save(T bean, String tableName);
	//读取
	public Object getById(String id, @SuppressWarnings("rawtypes") Class cls);
	public Object getById(String id, @SuppressWarnings("rawtypes") Class cls, String tableName);
	public Object findOne(T bean);
	public Object findOne(T bean, String tableName);
	
	


}
