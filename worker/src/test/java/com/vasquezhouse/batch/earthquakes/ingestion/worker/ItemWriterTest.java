package com.vasquezhouse.batch.earthquakes.ingestion.worker;

import com.vasquezhouse.batch.earthquakes.ingestion.worker.configuration.ItemWriterConfig;
import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.AlertLevel;
import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.AnalyticsEarthquake;
import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.EventType;
import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.MagnitudeType;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class ItemWriterTest {
    
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("test-analytics")
        .withUsername("test-username")
        .withPassword("test-password");
    
    @Test
    void writesToDatabase() {
        // arrange
        List<AnalyticsEarthquake> earthquakes = Arrays.asList(
            new AnalyticsEarthquake(
                Instant.now(),
                 BigDecimal.valueOf(7.2),
                "Pacific Ocean",
                "CA",
                "USA",
                true,
                800,
                MagnitudeType.mb,
                EventType.Earthquake,
                150,
                BigDecimal.valueOf(7.5),
                BigDecimal.valueOf(7.8),
                AlertLevel.Orange,
                "nc12345"
            ),
            new AnalyticsEarthquake(
                Instant.now(),
                BigDecimal.valueOf(6.5),
                "Atlantic Ocean",
                "FL",
                "USA",
                false,
                600,
                MagnitudeType.ms_20,
                EventType.QuarryBlast,
                100,
                BigDecimal.valueOf(6.0),
                BigDecimal.valueOf(6.5),
                AlertLevel.Yellow,
                "us12345"
            )
        );

        DataSource dataSource = createDataSource();
        createAnalyticsTablesInDatabase(dataSource);
        JdbcBatchItemWriter<AnalyticsEarthquake> writer = createWriter(dataSource);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        // act
        try {
            writer.write(new Chunk<>(earthquakes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // assert
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "earthquakes")).isEqualTo(2);

        List<Map<String, Object>> results = jdbcTemplate.queryForList("SELECT * FROM earthquakes");

        Map<String, Object> firstEarthquake = results.get(0);
        assertThat(firstEarthquake.get("magnitude")).isEqualTo(BigDecimal.valueOf(7.2));
        assertThat(firstEarthquake.get("place")).isEqualTo("Pacific Ocean");
        assertThat(firstEarthquake.get("state")).isEqualTo("CA");
        assertThat(firstEarthquake.get("tsunami")).isEqualTo(true);
        assertThat(firstEarthquake.get("mag_type")).isEqualTo("mb");
        assertThat(firstEarthquake.get("alert_level")).isEqualTo("Orange");

        Map<String, Object> secondEarthquake = results.get(1);
        assertThat(secondEarthquake.get("magnitude")).isEqualTo(BigDecimal.valueOf(6.5));
        assertThat(secondEarthquake.get("place")).isEqualTo("Atlantic Ocean");
        assertThat(secondEarthquake.get("state")).isEqualTo("FL");
        assertThat(secondEarthquake.get("tsunami")).isEqualTo(false);
        assertThat(secondEarthquake.get("mag_type")).isEqualTo("ms_20");
        assertThat(secondEarthquake.get("alert_level")).isEqualTo("Yellow");
    }
    
    @Test
    void handlesEmptyBatch() {
        // arrange
        DataSource dataSource = createDataSource();
        createAnalyticsTablesInDatabase(dataSource);
        JdbcBatchItemWriter<AnalyticsEarthquake> writer = createWriter(dataSource);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        // act
        try {
            writer.write(new Chunk<>(List.of()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        // assert
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "earthquakes")).isZero();
    }

    private static JdbcBatchItemWriter<AnalyticsEarthquake> createWriter(DataSource dataSource) {
        JdbcBatchItemWriter<AnalyticsEarthquake> writer = new ItemWriterConfig().itemWriter(dataSource);
        writer.afterPropertiesSet();
        return writer;
    }

    private void createAnalyticsTablesInDatabase(DataSource dataSource) {
        Flyway
            .configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration/analytics")
            .load()
            .migrate();
    }

    private static @NotNull DriverManagerDataSource createDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(postgres.getDriverClassName());
        dataSource.setUrl(postgres.getJdbcUrl());
        dataSource.setUsername(postgres.getUsername());
        dataSource.setPassword(postgres.getPassword());
        return dataSource;
    }
}
