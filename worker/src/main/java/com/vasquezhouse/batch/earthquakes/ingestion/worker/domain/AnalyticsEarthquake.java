package com.vasquezhouse.batch.earthquakes.ingestion.worker.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record AnalyticsEarthquake(
    Instant time,
    BigDecimal magnitude,
    String place,
    String state,
    String country,
    Boolean tsunami,
    Integer significance,
    MagnitudeType magnitudeType,
    EventType eventType,
    Integer feltReports,
    BigDecimal cdi,
    BigDecimal mmi,
    AlertLevel alertLevel,
    String sourceId
) {
}
