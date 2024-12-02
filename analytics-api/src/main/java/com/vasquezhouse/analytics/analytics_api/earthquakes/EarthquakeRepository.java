package com.vasquezhouse.analytics.analytics_api.earthquakes;

import com.vasquezhouse.analytics.analytics_api.earthquakes.model.Earthquake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EarthquakeRepository extends JpaRepository<Earthquake, Long>, JpaSpecificationExecutor<Earthquake> {
}
