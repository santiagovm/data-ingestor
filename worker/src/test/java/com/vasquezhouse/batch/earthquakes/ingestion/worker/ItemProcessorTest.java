package com.vasquezhouse.batch.earthquakes.ingestion.worker;

import com.vasquezhouse.batch.earthquakes.ingestion.worker.configuration.ItemProcessorConfig;
import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.AlertLevel;
import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.AnalyticsEarthquake;
import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.EventType;
import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.MagnitudeType;
import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.UsgsEarthquake;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ItemProcessorTest {
    
    @Test
    void processesCompleteItem() throws Exception {
        // arrange
        UsgsEarthquake usgsEarthquake = new UsgsEarthquake();
        usgsEarthquake.setTime(1648771200000L); // 2022-04-01
        usgsEarthquake.setMagnitude(BigDecimal.valueOf(4.5));
        usgsEarthquake.setPlace("9km NE of Aguanga, CA");
        usgsEarthquake.setTsunami(false);
        usgsEarthquake.setSignificance(100);
        usgsEarthquake.setMagType("ml");
        usgsEarthquake.setEventType("earthquake");
        usgsEarthquake.setFeltReports(50.0);
        usgsEarthquake.setCdi(BigDecimal.valueOf(4.5));
        usgsEarthquake.setMmi(BigDecimal.valueOf(3.2));
        usgsEarthquake.setAlertLevel("green");
        usgsEarthquake.setSourceId("abc123");
        
        CompositeItemProcessor<UsgsEarthquake, AnalyticsEarthquake> processor = createItemProcessor();

        // act
        AnalyticsEarthquake analyticsEarthquake = processor.process(usgsEarthquake);
        
        // assert
        assertThat(analyticsEarthquake).isNotNull();
        assertThat(analyticsEarthquake.time()).isEqualTo(Instant.ofEpochMilli(1648771200000L));
        assertThat(analyticsEarthquake.magnitude()).isEqualTo(BigDecimal.valueOf(4.5));
        assertThat(analyticsEarthquake.place()).isEqualTo("9km NE of Aguanga, CA");
        assertThat(analyticsEarthquake.state()).isEqualTo("CA");
        assertThat(analyticsEarthquake.country()).isEqualTo("US");
        assertThat(analyticsEarthquake.tsunami()).isEqualTo(false);
        assertThat(analyticsEarthquake.significance()).isEqualTo(100);
        assertThat(analyticsEarthquake.magnitudeType()).isEqualTo(MagnitudeType.ml);
        assertThat(analyticsEarthquake.eventType()).isEqualTo(EventType.Earthquake);
        assertThat(analyticsEarthquake.feltReports()).isEqualTo(50);
        assertThat(analyticsEarthquake.cdi()).isEqualTo(BigDecimal.valueOf(4.5));
        assertThat(analyticsEarthquake.mmi()).isEqualTo(BigDecimal.valueOf(3.2));
        assertThat(analyticsEarthquake.alertLevel()).isEqualTo(AlertLevel.Green);
        assertThat(analyticsEarthquake.sourceId()).isEqualTo("abc123");
    }

    @Test
    void failsWhenMissingData() {
        // arrange
        UsgsEarthquake usgsEarthquake = new UsgsEarthquake();

        CompositeItemProcessor<UsgsEarthquake, AnalyticsEarthquake> processor = createItemProcessor();

        // act/assert
        assertThatThrownBy(() -> processor.process(usgsEarthquake))
            .isInstanceOf(ValidationException.class);
    }
    
    @Test
    void failsWhenInvalidMagnitude() {
        // arrange
        UsgsEarthquake earthquake = getValidData();
        CompositeItemProcessor<UsgsEarthquake, AnalyticsEarthquake> processor = createItemProcessor();
        
        earthquake.setMagnitude(BigDecimal.valueOf(-20));
        
        // act/assert
        assertThatThrownBy(() -> processor.process(earthquake))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Field error in object 'item' on field 'magnitude': rejected value [-20]");
    }
    
    // add many more tests to cover all validations and transformations....

    private static CompositeItemProcessor<UsgsEarthquake, AnalyticsEarthquake> createItemProcessor() {
        return new ItemProcessorConfig().compositeItemProcessor();
    }
    
    private UsgsEarthquake getValidData() {
        UsgsEarthquake usgsEarthquake = new UsgsEarthquake();
        usgsEarthquake.setTime(1648771200000L); // 2022-04-01
        usgsEarthquake.setMagnitude(BigDecimal.valueOf(4.5));
        usgsEarthquake.setPlace("9km NE of Aguanga, CA");
        usgsEarthquake.setTsunami(false);
        usgsEarthquake.setSignificance(100);
        usgsEarthquake.setMagType("ml");
        usgsEarthquake.setEventType("earthquake");
        usgsEarthquake.setFeltReports(50.0);
        usgsEarthquake.setCdi(BigDecimal.valueOf(4.5));
        usgsEarthquake.setMmi(BigDecimal.valueOf(3.2));
        usgsEarthquake.setAlertLevel("green");
        usgsEarthquake.setSourceId("abc123");
        return usgsEarthquake;
    }
}
