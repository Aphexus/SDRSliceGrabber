package net.nicholaspurdy.sdrslicegrabber.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    private final Environment env;

    @Autowired
    public DataSourceConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public DataSource getDataSource() {

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(env.getProperty("slicegrabber.jdbcUrl"));
        config.setUsername(env.getProperty("slicegrabber.datasource.username"));
        config.setPassword(env.getProperty("slicegrabber.datasource.password"));
        config.setDriverClassName(env.getProperty("slicegrabber.datasource.driverName"));
        config.setMaximumPoolSize(env.getProperty("slicegrabber.datasource.maxPoolSize", Integer.class));

        return new HikariDataSource(config);

    }

}
