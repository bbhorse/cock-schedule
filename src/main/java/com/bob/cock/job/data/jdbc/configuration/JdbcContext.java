package com.bob.cock.job.data.jdbc.configuration;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public final class JdbcContext {
    
    private static final String COCK_RESOURCE = "cock-scheduler.properties";
    private static final String DRIVER = "cock.jdbc.driver";
    private static final String URL = "cock.jdbc.url";
    private static final String USER_NAME = "cock.jdbc.username";
    private static final String PWD = "cock.jdbc.password";

    private static volatile JdbcTemplate jdbcTemplate;
    
    public static JdbcTemplate jdbcTemplate() {
        if (null == jdbcTemplate) {
            synchronized (JdbcContext.class) {
                if (null == jdbcTemplate) {
                    jdbcTemplate = new JdbcTemplate(dataSource());
                }
            }
        }
        
        return jdbcTemplate;
    }
    
    private static DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ClasspathResource resource = new ClasspathResource(COCK_RESOURCE);
        ds.setDriverClassName(resource.getProperty(DRIVER));
        ds.setUrl(resource.getProperty(URL));
        ds.setUsername(resource.getProperty(USER_NAME));
        ds.setPassword(resource.getProperty(PWD));
        return ds;
    }
    
    private JdbcContext() {
    }
}
