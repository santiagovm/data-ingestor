package com.vasquezhouse.batch.job_mgmt_api;

import com.vasquezhouse.batch.job_mgmt_api.model.JobStatusResponse;
import com.vasquezhouse.batch.job_mgmt_api.test_utils.AnalyticsService;
import com.vasquezhouse.batch.job_mgmt_api.test_utils.JobMgmtService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Tag("end-to-end-test")
public class EndToEndTest {
    
    private final JobMgmtService jobMgmtService = new JobMgmtService();
    
    @Test
    void ingestEarthquakesFiles() {
        // arrange
        final int totalRecordsCount = 9_332;
        
        prepareTestFiles();
        int analyticsEarthquakesCountBefore = getAnalyticsEarthquakesCount();
        
        // act
        String jobId = jobMgmtService.triggerJob("test-e2e");

        // assert
        JobStatusResponse jobStatus = jobMgmtService.waitForJobToComplete(jobId);
        
        assertThat(jobStatus.workersCount())
            .withFailMessage("Expected multiple workers, but got %d", jobStatus.workersCount())
            .isGreaterThan(1);
        
        int expectedCount = analyticsEarthquakesCountBefore + totalRecordsCount;
        int analyticsEarthquakesCountAfter = getAnalyticsEarthquakesCount();
        assertThat(analyticsEarthquakesCountAfter).isEqualTo(expectedCount);
    }
    
    private int getAnalyticsEarthquakesCount() {
        return new AnalyticsService().getEarthquakesCount();
    }

    @SneakyThrows
    private void prepareTestFiles() {
        final String resourceName1 = "earthquakes-1.csv";
        final String resourceName2 = "earthquakes-2.csv";
        final String resourceName3 = "earthquakes-3.csv";
        
        ClassPathResource resource1 = new ClassPathResource(resourceName1);
        ClassPathResource resource2 = new ClassPathResource(resourceName2);
        ClassPathResource resource3 = new ClassPathResource(resourceName3);
        
        Path targetDir = Paths.get("../data/test-e2e");
        cleanDirectory(targetDir);
        
        Path targetPath1 = targetDir.resolve(resourceName1);
        Path targetPath2 = targetDir.resolve(resourceName2);
        Path targetPath3 = targetDir.resolve(resourceName3);
        
        Files.copy(resource1.getInputStream(), targetPath1, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(resource2.getInputStream(), targetPath2, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(resource3.getInputStream(), targetPath3, StandardCopyOption.REPLACE_EXISTING);
        
        log.info(" >>> test file created: {}", targetPath1);
        log.info(" >>> test file created: {}", targetPath2);
        log.info(" >>> test file created: {}", targetPath3);
    }
    
    @SneakyThrows
    private void cleanDirectory(Path directory) {
        Files.createDirectories(directory);
        try (var files = Files.list(directory)) {
            files.forEach(file -> {
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
