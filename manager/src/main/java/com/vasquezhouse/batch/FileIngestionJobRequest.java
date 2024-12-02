package com.vasquezhouse.batch;

import lombok.Data;

import java.io.Serializable;

// using serializable class instead of record because this class is to send messages to Spring Batch that does not
// work well with JSON serialization
// Also, keeping this class in a package common to multiple apps so java serialization does not run into namespaces issues
@Data
public class FileIngestionJobRequest implements Serializable {
    private String jobId;
    private String jobType;
    private String dataDirectory;
}
