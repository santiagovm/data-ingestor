package com.vasquezhouse.batch.earthquakes.ingestion.manager.configuration;

import com.vasquezhouse.batch.earthquakes.ingestion.manager.FilePartitioner;
import com.vasquezhouse.batch.earthquakes.ingestion.manager.JobListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningManagerStepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;

@Slf4j
@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
@RequiredArgsConstructor
public class BatchConfig {
    
    private final RemotePartitioningManagerStepBuilderFactory stepBuilderFactory;
    private final FilePartitioner filePartitioner;
    private final JobRepository jobRepository;
    private final JobListener jobListener;
    
    @Bean
    public Job earthquakesIngestionJob(Step managerStep) {
        return new JobBuilder("earthquakesIngestionJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .listener(jobListener)
            .start(managerStep)
            .build();
    }

    @Bean
    public Step managerStep(DirectChannel requestsChannel, DirectChannel repliesChannel) {
        return stepBuilderFactory.get("managerStep")
            .partitioner("workerStep", filePartitioner)
            .outputChannel(requestsChannel)
            .inputChannel(repliesChannel)
            .build();
    }
}
