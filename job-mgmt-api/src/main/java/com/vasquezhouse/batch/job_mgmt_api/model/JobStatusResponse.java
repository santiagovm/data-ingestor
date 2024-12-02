package com.vasquezhouse.batch.job_mgmt_api.model;

import java.time.Instant;

public record JobStatusResponse(
    String jobId,
    Long jobExecutionId,
    String status,
    Instant startTime,
    Instant endTime,
    Integer workersCount
) {
}
