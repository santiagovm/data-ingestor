package com.vasquezhouse.batch.earthquakes.ingestion.worker.domain;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Data
public class UsgsEarthquake {
    @NotNull(message = "Time is required")
    @Min(value = 0, message = "Time must be after 1970")
    @Max(value = 253402300799999L, message = "Time must be before year 9999")
    private Long time;

    @DecimalMin(value = "-2.0", message = "Magnitude must be >= -2.0")
    @DecimalMax(value = "10.0", message = "Magnitude must be <= 10.0")
    private BigDecimal magnitude;

    @NotBlank(message = "Place is required")
    private String place;

    @NotNull(message = "Tsunami indicator is required")
    private Boolean tsunami;

    @Range(min = 0, max = 3000, message = "Significance must be between 0 and 3000")
    private Integer significance;

    @Pattern(regexp = "^(mb|md|mh|ml|mb_lg|ms|ms_20|mw|mwb|mwr|mww)?$", message = "Invalid magnitude type")
    private String magType;

    @Pattern(regexp = "^(earthquake|quarry blast|ice quake|explosion|other event)$", message = "Invalid event type")
    private String eventType;

    @DecimalMin(value = "0.0", message = "Felt reports must be >= 0")
    private Double feltReports;

    @DecimalMin(value = "0.0", message = "CDI must be >= 0")
    @DecimalMax(value = "12.0", message = "CDI must be <= 12.0")
    private BigDecimal cdi;

    @DecimalMin(value = "0.0", message = "MMI must be >= 0")
    @DecimalMax(value = "12.0", message = "MMI must be <= 12.0")
    private BigDecimal mmi;

    @Pattern(regexp = "^(green|yellow|orange|red)?$", message = "Invalid alert level")
    private String alertLevel;

    @NotBlank(message = "Source ID is required")
    private String sourceId;
}
