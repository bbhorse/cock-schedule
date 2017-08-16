package com.bob.cock.job.data.jdbc;

import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bob.cock.job.data.jdbc.configuration.JdbcContext;

public class BaseDao<T> {
    
    private static final String GET_CUR_TIMESTRAP = "select now(3)";
    
    protected JdbcTemplate jdbcTemplate = JdbcContext.jdbcTemplate();
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public T queryObject(Class<T> clazz, String sql, Object... args) {
        return (T)jdbcTemplate.queryForObject(sql, args, new BeanPropertyRowMapper(clazz));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<T> queryList(Class<T> clazz, String sql, Object... args) {
        return (List<T>)jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(clazz));
    }

    public int save(String sql, Object[] args, int[] types) {
        return jdbcTemplate.update(sql, args, types);
    }

    public int update(String sql, Object[] args, int[] types) {
        return jdbcTemplate.update(sql, args, types);
    }

    public int delete(String sql, Object[] args, int[] types) {
        return jdbcTemplate.update(sql, args, types);
    }
    
    public Long getCurTimestamp() {
        return jdbcTemplate.queryForObject(GET_CUR_TIMESTRAP, Date.class).getTime();
    }
}
