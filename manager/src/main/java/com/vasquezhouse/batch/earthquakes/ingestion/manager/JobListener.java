package com.vasquezhouse.batch.earthquakes.ingestion.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info(" >>> Job started: {} at {}",
            jobExecution.getJobInstance().getJobName(),
            jobExecution.getStartTime());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info(" >>> Job finished: {} at {}",
            jobExecution.getJobInstance().getJobName(),
            jobExecution.getEndTime());
        
        log.info(" >>> Status: {}", jobExecution.getStatus());

        // Log statistics for each step
        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            log.info(" >>> Step: {} - Read: {}, Processed: {}, Written: {}, Skipped: {}",
                stepExecution.getStepName(),
                stepExecution.getReadCount(),
                stepExecution.getFilterCount(),
                stepExecution.getWriteCount(),
                stepExecution.getSkipCount());
        }
    }
}
