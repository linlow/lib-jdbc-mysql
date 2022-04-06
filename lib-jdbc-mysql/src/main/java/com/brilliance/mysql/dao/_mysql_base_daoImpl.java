package com.brilliance.mysql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class _mysql_base_daoImpl<T> extends NamedParameterJdbcDaoSupport implements _mysql_base_dao<T> {
	protected static Logger logger = LoggerFactory.getLogger(_mysql_base_daoImpl.class);

	/* ##### 注入数据源 ############ */
	@Resource(name = "dataSource")
	public void setDS(DataSource dataSource) {
		setDataSource(dataSource);
	}

	/* #########大数据查询############ */
	public void doBigQuery(final String sql, RowCallbackHandler rc) {
		super.getJdbcTemplate().query(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				ps.setFetchSize(100);
				return ps;
			}
		}, rc);
	}
	
	/* #########读取指定数据############ */
	@Override
	public Object getById(String id, Class cls) {
		return getById(id, cls, cls.getSimpleName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getById(String id, Class cls, String tableName) {
		String sql = mysqlDaoUtil.getQueryIdSql(cls, tableName);
		logger.debug(sql);
		try {
			return super.getJdbcTemplate().queryForObject(sql, new Object[] { id },
					BeanPropertyRowMapper.newInstance(cls));
		} catch (Exception e) {
			logger.debug("没有查询到结果，id is：" + id);
			return null;
		}
	}

	@Override
	public Object findOne(T bean) {
		return findOne(bean, bean.getClass().getSimpleName());
	}

	@Override
	public Object findOne(T bean, String tableName) {
		String sql = mysqlDaoUtil.getQuerySql(bean, tableName);
		logger.debug(sql);
		try {
			return super.getNamedParameterJdbcTemplate().queryForObject(sql, new BeanPropertySqlParameterSource(bean),
					BeanPropertyRowMapper.newInstance(bean.getClass()));
		} catch (Exception e) {
			logger.debug("没有查询到结果，bean is：" + bean.toString());
			return null;
		}
	}

	/* ##### 获取数据全部列表 ############ */
	@Override
	public <T> List<T> getList(T bean) {
		return getList(bean, Sort.unsorted(), bean.getClass().getSimpleName());
	}

	@Override
	public <T> List<T> getList(T bean, String tableName) {
		return getList(bean, Sort.unsorted(), tableName);
	}

	@Override
	public <T> List<T> getList(T bean, Sort sort) {
		return getList(bean, sort, bean.getClass().getSimpleName());
	}

	@Override
	public <T> List<T> getList(T bean, Sort sort, String tableName) {
		String sql = mysqlDaoUtil.getQuerySql(bean, tableName, sort);
		logger.debug(sql);
		return (List<T>) super.getNamedParameterJdbcTemplate().query(sql, new BeanPropertySqlParameterSource(bean),
				BeanPropertyRowMapper.newInstance(bean.getClass()));
	}

	@Override
	public <T> List<T> getList(T bean, Sort sort, int iMax) {
		return getList(bean, sort, bean.getClass().getSimpleName(), iMax);
	}

	@Override
	public <T> List<T> getList(T bean, Sort sort, String tableName, int iMax) {
		String sql = mysqlDaoUtil.getQuerySql(bean, tableName, sort);
		sql = sql + " and LIMIT " + iMax;
		logger.debug(sql);
		return (List<T>) super.getNamedParameterJdbcTemplate().query(sql, new BeanPropertySqlParameterSource(bean),
				BeanPropertyRowMapper.newInstance(bean.getClass()));
	}

	/* ##### 统计数量 ############ */
	@Override
	public long count_num(T bean) {
		return count_num(bean, bean.getClass().getSimpleName());
	}

	@Override
	public long count_num(T bean, String tableName) {
		String sql = mysqlDaoUtil.getQuerySql(bean, tableName);
		sql = sql.replace("select *", "select count(1) as num");
		logger.debug(sql);
		return super.getNamedParameterJdbcTemplate().queryForObject(sql, new BeanPropertySqlParameterSource(bean),
				new RowMapper<Long>() {
					@Override
					public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getLong("num");
					}
				});
	}

	/* #########非排序的分页查询############ */
	@Override
	public <T> Page<T> getPage(T bean, Pageable pageable) {
		return getPage(bean, pageable, Sort.unsorted(), bean.getClass().getSimpleName());
	}

	@Override
	public <T> Page<T> getPage(T bean, Pageable pageable, String tableName) {
		return getPage(bean, pageable, Sort.unsorted(), tableName);
	}

	@Override
	public <T> Page<T> getPage(T bean, Pageable pageable, Sort sort) {
		return getPage(bean, pageable, sort, bean.getClass().getSimpleName());
	}

	/* #########排序的分页查询############ */
	@Override
	public <T> Page<T> getPage(T bean, Pageable pageable, Sort sort, String tableName) {
		String base_sql = mysqlDaoUtil.getQuerySql(bean, tableName, sort);
		String sql = base_sql + " LIMIT " + pageable.getPageSize() + " OFFSET "
				+ pageable.getPageNumber() * pageable.getPageSize();// skip
		logger.debug(sql);
		List result = super.getNamedParameterJdbcTemplate().query(sql, new BeanPropertySqlParameterSource(bean),
				BeanPropertyRowMapper.newInstance(bean.getClass()));
		sql = base_sql.replace("select *", "select count(1) as num");
		logger.debug(sql);
		// long l_total = this.count_num(bean , tableName);//为什么不可以
		long l_total = super.getNamedParameterJdbcTemplate().queryForObject(sql,
				new BeanPropertySqlParameterSource(bean), new RowMapper<Long>() {
					@Override
					public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getLong("num");
					}
				});
		return new PageImpl<T>(result, pageable, l_total);
	}

	/* #########插入新数据############ */
	@Override
	public int insert(T bean) {
		final String sql = mysqlDaoUtil.getInserSql(bean, bean.getClass().getSimpleName());
		logger.debug(sql);
		return super.getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(bean));
	}

	@Override
	public int insert(T bean, String tableName) {
		final String sql = mysqlDaoUtil.getInserSql(bean, tableName);
		logger.debug(sql);
		return super.getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(bean));
	}

	@Override
	public int insertAll(List<T> list) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* #########修改数据############ */
	@Override
	public int save(T bean) {
		return save(bean, bean.getClass().getSimpleName());
	}

	@Override
	public int save(T bean, String tableName) {
		final String sql = mysqlDaoUtil.getUpdateSql(bean, tableName);
		logger.debug(sql);
		return super.getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(bean));
	}
}
