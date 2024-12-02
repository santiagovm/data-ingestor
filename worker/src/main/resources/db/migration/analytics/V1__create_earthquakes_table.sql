CREATE TABLE earthquakes
(
    id           SERIAL PRIMARY KEY,
    time         TIMESTAMP WITH TIME ZONE NOT NULL,
    magnitude    DECIMAL(3, 1),
    place        VARCHAR(255)             NOT NULL,
    state        VARCHAR(255),
    country      VARCHAR(255)             NOT NULL,
    tsunami      BOOLEAN                  DEFAULT false,
    significance INTEGER,
    mag_type     VARCHAR(10),
    event_type   VARCHAR(50)              DEFAULT 'earthquake',
    felt_reports INTEGER                  DEFAULT 0,
    cdi          DECIMAL(3, 1),
    mmi          DECIMAL(3, 1),
    alert_level  VARCHAR(10),
    source_id    VARCHAR(20)              NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Compound index for our most common query pattern: filtering by time and magnitude, sorting by time
-- The DESC ordering helps with our common DESC sorts
CREATE INDEX idx_earthquakes_time_mag ON earthquakes (time DESC, magnitude);

-- Compound index for pagination: ensures efficient keyset pagination queries
CREATE INDEX idx_earthquakes_time_id ON earthquakes (time DESC, id DESC);

-- Partial index for significant earthquakes: dramatically reduces index size while maintaining performance
-- for queries that focus on larger earthquakes
CREATE INDEX idx_significant_earthquakes ON earthquakes (time, magnitude, country)
    WHERE magnitude >= 4.0;

-- Specialized index for tsunami-related queries. Including magnitude since it's commonly used together
CREATE INDEX idx_earthquakes_tsunami ON earthquakes (tsunami, magnitude)
    WHERE tsunami = true;

-- Covering index for frequently accessed columns: Eliminates the need to access the main table for some queries
CREATE INDEX idx_earthquakes_common_cols ON earthquakes
    (time, magnitude, country)
    INCLUDE (place, alert_level, felt_reports);

-- Materialized view to maintain good performance for recent data queries
CREATE MATERIALIZED VIEW recent_earthquakes AS
SELECT *
FROM earthquakes
WHERE time >= NOW() - INTERVAL '3 months'
WITH DATA;

CREATE INDEX idx_recent_earthquakes_mv ON recent_earthquakes (time DESC, magnitude);

-- Refresh the view periodically (via cron job or similar)
-- REFRESH MATERIALIZED VIEW recent_earthquakes;
