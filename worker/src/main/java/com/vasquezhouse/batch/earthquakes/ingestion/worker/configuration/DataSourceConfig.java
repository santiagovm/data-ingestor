package com.vasquezhouse.batch.earthquakes.ingestion.worker.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.batch")
    public HikariConfig batchHikariConfig() {
        return new HikariConfig();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.analytics")
    public HikariConfig analyticsHikariConfig() {
        return new HikariConfig();
    }

    @Bean
    @Primary
    public DataSource batchDataSource() {
        return new HikariDataSource(batchHikariConfig());
    }

    @Bean
    public DataSource analyticsDataSource() {
        return new HikariDataSource(analyticsHikariConfig());
    }

    @Bean(name = "batchTransactionManager")
    @Primary
    public PlatformTransactionManager batchTransactionManager(
        @Qualifier("batchDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "analyticsTransactionManager")
    public PlatformTransactionManager analyticsTransactionManager(
        @Qualifier("analyticsDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
