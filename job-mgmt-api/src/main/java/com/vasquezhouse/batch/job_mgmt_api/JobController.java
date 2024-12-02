package com.vasquezhouse.batch.job_mgmt_api;

import com.vasquezhouse.batch.FileIngestionJobRequest;
import com.vasquezhouse.batch.job_mgmt_api.model.ExecutionFailure;
import com.vasquezhouse.batch.job_mgmt_api.model.RunJobRequest;
import com.vasquezhouse.batch.job_mgmt_api.model.RunJobResponse;
import com.vasquezhouse.batch.job_mgmt_api.model.JobStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobController {
    
    private final RabbitTemplate rabbitTemplate;
    private final JobService jobService;

    @Value("${app.jobs-exchange}")
    private String exchangeName;
    
    @PostMapping("/jobs/earthquakes-ingestion/run")
    public ResponseEntity<RunJobResponse> runEarthquakesIngestionJob(@RequestBody RunJobRequest request) {
        FileIngestionJobRequest fileIngestionJobRequest = new FileIngestionJobRequest();
        fileIngestionJobRequest.setJobId(request.jobId());
        fileIngestionJobRequest.setDataDirectory(request.dataDirectory());
        fileIngestionJobRequest.setJobType("earthquake-ingestion-job");
        
        rabbitTemplate.convertAndSend(exchangeName, "", fileIngestionJobRequest);
        
        return ResponseEntity.ok(new RunJobResponse(request.jobId(), "run request submitted"));
    }
    
    @GetMapping("/jobs/{jobId}/status")
    public ResponseEntity<JobStatusResponse> getJobStatus(@PathVariable String jobId) {
        return jobService.findJobStatus(jobId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/execution-failures/{executionId}")
    public ResponseEntity<ExecutionFailure> getExecutionFailure(@PathVariable Long executionId) {
        return jobService.findExecutionFailure(executionId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
