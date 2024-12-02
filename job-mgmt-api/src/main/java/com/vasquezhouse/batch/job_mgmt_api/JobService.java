package com.vasquezhouse.batch.job_mgmt_api;

import com.vasquezhouse.batch.job_mgmt_api.model.ExecutionFailure;
import com.vasquezhouse.batch.job_mgmt_api.model.JobStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobService {

    private final JdbcTemplate jdbcTemplate;
    private final JobExplorer jobExplorer;

    public Optional<JobStatusResponse> findJobStatus(String jobId) {
        String sql = """
                SELECT je.JOB_EXECUTION_ID
                FROM BATCH_JOB_EXECUTION je
                JOIN BATCH_JOB_EXECUTION_PARAMS jp
                    ON je.JOB_EXECUTION_ID = jp.JOB_EXECUTION_ID
                WHERE jp.PARAMETER_NAME = 'jobId'
                AND jp.PARAMETER_VALUE = ?
                ORDER BY je.JOB_EXECUTION_ID DESC
            """;

        RowMapper<Long> rowMapper = (rs, rowNum) -> rs.getLong("JOB_EXECUTION_ID");

        List<Long> executionIds = jdbcTemplate.query(sql, rowMapper, jobId);

        if (executionIds.isEmpty()) return Optional.empty();

        Long jobExecutionId = executionIds.getFirst();
        JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);

        if (jobExecution == null) return Optional.empty();

        Set<String> workers = jobExecution.getStepExecutions()
            .stream()
            .filter(step -> step.getStepName().startsWith("workerStep"))
            .filter(step -> {
                if (!step.getExecutionContext().containsKey("worker-id")) return false;
                String workerId = step.getExecutionContext().get("worker-id", String.class);
                return workerId != null && !workerId.isEmpty();
            })
            .map(step -> step.getExecutionContext().get("worker-id", String.class))
            .collect(Collectors.toSet());

        return Optional.of(new JobStatusResponse(
            jobId,
            jobExecutionId,
            jobExecution.getStatus().name(),
            toInstant(jobExecution.getStartTime()),
            toInstant(jobExecution.getEndTime()),
            workers.size()
        ));
    }
    
    private static Instant toInstant(LocalDateTime value) {
        if (value == null) return null;
        return value.toInstant(ZoneOffset.UTC);
    }

    public Optional<ExecutionFailure> findExecutionFailure(Long executionId) {
        JobExecution jobExecution = jobExplorer.getJobExecution(executionId);
        if (jobExecution == null) return Optional.empty();

        String exitCode = jobExecution.getExitStatus().getExitCode();
        String exitDescription = getExitDescription(jobExecution);

        ExecutionFailure failure = new ExecutionFailure(exitCode, exitDescription);
        return Optional.of(failure);
    }

    private static String getExitDescription(JobExecution jobExecution) {
        StringBuilder descriptionStringBuilder = new StringBuilder();

        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            if (stepExecution.getStatus() != BatchStatus.FAILED) continue;

            String stepDescription = String.format(
                "[%s]: %s",
                stepExecution.getStepName(),
                stepExecution.getExitStatus().getExitDescription()
            );

            descriptionStringBuilder.append(stepDescription);
        }
        return descriptionStringBuilder.toString();
    }
}
