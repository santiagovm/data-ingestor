package com.vasquezhouse.batch.job_mgmt_api.test_utils;

import com.vasquezhouse.batch.job_mgmt_api.model.JobStatusResponse;
import com.vasquezhouse.batch.job_mgmt_api.model.RunJobRequest;
import com.vasquezhouse.batch.job_mgmt_api.model.RunJobResponse;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class JobMgmtService {

    private final WebClient webClient = WebClient.builder()
        .baseUrl(PropertyReader.getProperty("test.job-mgmt-api-url"))
        .build();

    public String triggerJob(String dataDirectory) {
        String jobId = generateJobId();
        RunJobRequest request = new RunJobRequest(jobId, dataDirectory);

        webClient
            .post()
            .uri("/api/jobs/earthquakes-ingestion/run")
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(RunJobResponse.class)
            .block();

        return jobId;
    }

    public JobStatusResponse waitForJobToComplete(String jobId) {

        AtomicReference<JobStatusResponse> finalResponse = new AtomicReference<>();

        Awaitility.await()
            .atMost(Duration.ofSeconds(15))
            .pollInterval(Duration.ofSeconds(2))
            .pollDelay(Duration.ofSeconds(3))
            .ignoreException(WebClientResponseException.NotFound.class)
            .until(() -> {
                JobStatusResponse response = webClient
                    .get()
                    .uri("/api/jobs/{jobId}/status", jobId)
                    .retrieve()
                    .onStatus(
                        status -> status.equals(HttpStatus.NOT_FOUND),
                        error -> Mono.error(new WebClientResponseException(404, "Not Found", null, null, null))
                    )
                    .bodyToMono(JobStatusResponse.class)
                    .block();
                
                log.info(" >>> job status: {}", response);
                
                finalResponse.set(response);
                return "COMPLETED".equals(response.status());
            });
        
        return finalResponse.get();
    }

    private String generateJobId() {
        return "test-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));
    }
}
