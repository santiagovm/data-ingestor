package com.vasquezhouse.analytics.analytics_api.earthquakes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EarthquakeStats {
    private long count;
    private double averageMagnitude;
    private double maxMagnitude;
    private long tsunamiCount;
}
