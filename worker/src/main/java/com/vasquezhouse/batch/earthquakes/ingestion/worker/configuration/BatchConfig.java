package com.vasquezhouse.batch.earthquakes.ingestion.worker.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig extends DefaultBatchConfiguration {

    @Qualifier("batchDataSource")
    private final DataSource batchDataSource;
    
    @Qualifier("analyticsTransactionManager")
    private final PlatformTransactionManager batchTransactionManager;

    @Override
    protected DataSource getDataSource() {
        return batchDataSource;
    }
    
    @Override
    protected PlatformTransactionManager getTransactionManager() {
        return batchTransactionManager;
    }
}
