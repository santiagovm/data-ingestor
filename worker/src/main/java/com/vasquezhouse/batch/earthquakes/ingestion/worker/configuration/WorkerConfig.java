package com.vasquezhouse.batch.earthquakes.ingestion.worker.configuration;

import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.AnalyticsEarthquake;
import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.UsgsEarthquake;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.transaction.PlatformTransactionManager;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Configuration
@EnableBatchIntegration
@RequiredArgsConstructor
public class WorkerConfig {

    private final PlatformTransactionManager transactionManager;
    private final RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory;

    @Value("${app.chunk-size}")
    private int chunkSize;

    @Bean
    public Step workerStep(DirectChannel requestsChannel,
                           DirectChannel repliesChannel,
                           FlatFileItemReader<UsgsEarthquake> itemReader,
                           CompositeItemProcessor<UsgsEarthquake, AnalyticsEarthquake> itemProcessor,
                           JdbcBatchItemWriter<AnalyticsEarthquake> itemWriter
    ) {
        return workerStepBuilderFactory.get("workerStep")
            .inputChannel(requestsChannel)
            .outputChannel(repliesChannel)
            .<UsgsEarthquake, AnalyticsEarthquake>chunk(chunkSize, transactionManager)
            .reader(itemReader)
            .processor(itemProcessor)
            .writer(itemWriter)
            .listener(new StepExecutionListener() {
                @Override
                public void beforeStep(StepExecution stepExecution) {
                    StepExecutionListener.super.beforeStep(stepExecution);
                    stepExecution.getExecutionContext().putString("worker-id", getWorkerId());
                }
            })
            .build();
    }

    private static String getWorkerId() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
}
