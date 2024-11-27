package com.vasquezhouse.earthquakes.ingestion.worker;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
@RequiredArgsConstructor
public class WorkerConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Step workerStep(DataSource dataSource) {
        return new StepBuilder("workerStep", jobRepository)
            // todo: make chunk size configurable
            .<Earthquake, Earthquake>chunk(10, transactionManager)
            .reader(itemReader(null)) // actual filename will be injected
            .processor(processor())
            .writer(writer(dataSource))
            .build();
    }

    @Bean
    @StepScope // allows late binding of the file parameter
    public FlatFileItemReader<Earthquake> itemReader(@Value("#{stepExecutionContext[filename]}") String filename) {
        return new FlatFileItemReaderBuilder<Earthquake>()
            .name("earthquakeItemReader")
            .resource(new FileSystemResource(filename))
            .delimited()
            // todo: use correct field names
            .names("firstName", "lastName", "email")
            .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(Earthquake.class);
            }})
            .linesToSkip(1) // skip header
            .strict(true) // fail if file format is incorrect
            .build();
    }

    @Bean
    public EarthquakeItemProcessor processor() {
        return new EarthquakeItemProcessor();
    }
    
    @Bean
    public JdbcBatchItemWriter<Earthquake> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Earthquake>()
            // todo: use correct sql
            .sql("insert into earthquakes (first_name, last_name, email) values (:firstName, :lastName, :email)")
            .dataSource(dataSource)
            .beanMapped()
            .build();
    }
}
