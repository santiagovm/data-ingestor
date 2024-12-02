package com.vasquezhouse.analytics.analytics_api.earthquakes.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class EarthquakeFilter {
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private Double minMagnitude;
    private Double maxMagnitude;
    private String country;
    private Boolean tsunami;
}
