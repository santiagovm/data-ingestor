package com.vasquezhouse.batch.earthquakes.ingestion.worker.configuration;

import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.AlertLevel;
import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.AnalyticsEarthquake;
import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.EventType;
import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.MagnitudeType;
import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.UsaStatesMap;
import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.UsgsEarthquake;
import lombok.SneakyThrows;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.List;

@Configuration
public class ItemProcessorConfig {

    @Bean
    public CompositeItemProcessor<UsgsEarthquake, AnalyticsEarthquake> compositeItemProcessor() {
        List<ItemProcessor<?, ?>> delegates = List.of(
            validatingItemProcessor(),
            transformingItemProcessor()
        );

        CompositeItemProcessor<UsgsEarthquake, AnalyticsEarthquake> processor = new CompositeItemProcessor<>();
        processor.setDelegates(delegates);
        return processor;
    }

    @SneakyThrows
    @Bean
    public BeanValidatingItemProcessor<UsgsEarthquake> validatingItemProcessor() {
        BeanValidatingItemProcessor<UsgsEarthquake> processor = new BeanValidatingItemProcessor<>();
        processor.setFilter(false); // false: throw exceptions for invalid items
        processor.afterPropertiesSet();
        return processor;
    }

    @Bean
    public ItemProcessor<UsgsEarthquake, AnalyticsEarthquake> transformingItemProcessor() {
        return new ItemProcessor<>() {
            @Override
            public AnalyticsEarthquake process(UsgsEarthquake item) {
                return new AnalyticsEarthquake(
                    Instant.ofEpochMilli(item.getTime()),
                    item.getMagnitude(),
                    item.getPlace(),
                    toUsaState(item.getPlace()),
                    toCountry(item.getPlace()),
                    item.getTsunami(),
                    item.getSignificance(),
                    mapMagnitudeType(item.getMagType()),
                    mapEventType(item.getEventType()),
                    mapInteger(item.getFeltReports()),
                    item.getCdi(),
                    item.getMmi(),
                    mapAlertLevel(item.getAlertLevel()),
                    item.getSourceId()
                );
            }

            // Place examples
            // "M 1.4 - 9km NE of Aguanga, CA"
            // "M 1.1 - 14km NE of East Quincy, California"
            // "M 3.0 - 105km NNW of San Antonio, Puerto Rico"
            // "M 4.6 - 213km SE of Hachijo-jima, Japan"

            private static String toUsaState(String place) {
                String lastPart = place.substring(place.lastIndexOf(",") + 1).trim();
                return UsaStatesMap.findUsaState(lastPart);
            }

            private static String toCountry(String place) {
                String lastPart = place.substring(place.lastIndexOf(",") + 1).trim();
                return UsaStatesMap.isUsaState(lastPart)
                    ? "US"
                    : lastPart;
            }

            private static Integer mapInteger(Double value) {
                return value == null
                    ? null
                    : value.intValue();
            }

            private static MagnitudeType mapMagnitudeType(String value) {
                if (value == null || value.trim().isEmpty()) return null;
                return MagnitudeType.valueOf(value);
            }

            private static EventType mapEventType(String value) {
                if (value == null || value.trim().isEmpty()) return null;
                return switch (value.toLowerCase()) {
                    case "earthquake" -> EventType.Earthquake;
                    case "quarry blast" -> EventType.QuarryBlast;
                    case "ice quake" -> EventType.IceQuake;
                    case "explosion" -> EventType.Explosion;
                    case "other event" -> EventType.OtherEvent;
                    default -> throw new IllegalArgumentException("Invalid event type: " + value);
                };
            }

            private static AlertLevel mapAlertLevel(String value) {
                if (value == null || value.trim().isEmpty()) return null;
                return switch (value.toLowerCase()) {
                    case "green" -> AlertLevel.Green;
                    case "yellow" -> AlertLevel.Yellow;
                    case "orange" -> AlertLevel.Orange;
                    case "red" -> AlertLevel.Red;
                    default -> throw new IllegalArgumentException("Invalid alert level: " + value);
                };
            }
        };
    }
}
