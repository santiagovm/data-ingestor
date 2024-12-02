package com.vasquezhouse.analytics.analytics_api.earthquakes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "earthquakes")
public class Earthquake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private ZonedDateTime time;
    private BigDecimal magnitude;
    private String place;
    private String state;
    private String country;
    private Boolean tsunami;
    private Integer significance;

    @Column(name = "mag_type")
    private String magType;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "felt_reports")
    private Integer feltReports;

    private BigDecimal cdi;
    private BigDecimal mmi;

    @Column(name = "alert_level")
    private String alertLevel;

    @Column(name = "source_id")
    private String sourceId;
}
