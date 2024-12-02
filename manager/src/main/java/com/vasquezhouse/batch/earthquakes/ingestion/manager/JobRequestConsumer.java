package com.vasquezhouse.batch.earthquakes.ingestion.manager;

import com.vasquezhouse.batch.FileIngestionJobRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobRequestConsumer {

    private final JobLauncher jobLauncher;
    private final Job job;
    
    private static final String JOB_TYPE_TO_PROCESS = "earthquake-ingestion-job";

    @RabbitListener(queues = "${app.job-requests-queue}")
    public void consumeMessage(FileIngestionJobRequest request) {
        log.debug(" >>> consuming job request: [{}]", request);
        if (!request.getJobType().equals(JOB_TYPE_TO_PROCESS)) {
            log.debug(" >>> ignored job request, does not match job type: [{}]", JOB_TYPE_TO_PROCESS);
            return;
        }
        
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("jobId", request.getJobId(), true)
            .addString("dataDirectory", request.getDataDirectory())
            .toJobParameters();

        try {
            log.info(" >>> launching {}...", JOB_TYPE_TO_PROCESS);
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            log.debug(" >>> job finished. execution id: [{}] status: [{}]", jobExecution.getJobId(), jobExecution.getStatus());
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            throw new RuntimeException(e);
        }
    }
}
