package com.vasquezhouse.batch.earthquakes.ingestion.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@StepScope
public class FilePartitioner implements Partitioner {

    @Value("${app.input-directory}")
    private String inputDirectory;

    @Value("#{jobParameters['dataDirectory']}")
    private String subDirectory;
    
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        String sourceDirectory = inputDirectory + "/" + subDirectory;
        File[] files = new File(sourceDirectory)
            .listFiles((dir, name) -> name.startsWith("earthquakes") && name.endsWith(".csv"));

        Map<String, ExecutionContext> partitions = new HashMap<>();

        if (files != null) {
            log.info(" >>> Found {} files to process in {}", files.length, sourceDirectory);
            
            for (int i = 0; i < files.length; i++) {
                ExecutionContext context = new ExecutionContext();
                String filename = files[i].getAbsolutePath();
                context.putString("filename", filename);

                // Partition name must be unique
                String partitionName = "partition" + i;
                partitions.put(partitionName, context);
                log.info(" >>> Created partition: {} for file: {}", partitionName, filename);
            }
        } else {
            log.warn(" >>> No files found in directory: {}", sourceDirectory);
        }

        return partitions;
    }
}
