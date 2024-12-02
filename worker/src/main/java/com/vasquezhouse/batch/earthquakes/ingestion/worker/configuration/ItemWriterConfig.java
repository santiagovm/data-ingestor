package com.vasquezhouse.batch.earthquakes.ingestion.worker.configuration;

import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.AnalyticsEarthquake;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.sql.DataSource;
import java.sql.Types;

@Configuration
public class ItemWriterConfig {
    
    @Bean
    public JdbcBatchItemWriter<AnalyticsEarthquake> itemWriter(@Qualifier("analyticsDataSource") DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<AnalyticsEarthquake>()
            .sql("insert into earthquakes (time, magnitude, place, state, country, tsunami, " +
                "significance, mag_type, event_type, felt_reports, cdi, mmi, alert_level, source_id) " +
                "values (:time, :magnitude, :place, :state, :country, :tsunami, " +
                ":significance, :magType, :eventType, :feltReports, :cdi, :mmi, :alertLevel, :sourceId)")
            .dataSource(dataSource)
            // bean mapper did not work because of the instant type for time property, using manual mapping as workaround
            .itemSqlParameterSourceProvider(item -> {
                MapSqlParameterSource source = new MapSqlParameterSource();
                source.addValue("time", item.time(), Types.OTHER);
                source.addValue("magnitude", item.magnitude());
                source.addValue("place", item.place());
                source.addValue("state", item.state());
                source.addValue("country", item.country());
                source.addValue("tsunami", item.tsunami());
                source.addValue("significance", item.significance());
                source.addValue("magType", item.magnitudeType(), Types.VARCHAR);
                source.addValue("eventType", item.eventType(), Types.VARCHAR);
                source.addValue("feltReports", item.feltReports());
                // cdi is a list of comma-separated values we could use a CompositeItemWriter to insert them in a child table
                source.addValue("cdi", item.cdi());
                source.addValue("mmi", item.mmi());
                source.addValue("alertLevel", item.alertLevel(), Types.VARCHAR);
                source.addValue("sourceId", item.sourceId());
                return source;
            })
            .build();
    }
}
