package com.vasquezhouse.analytics.analytics_api.earthquakes;

import com.vasquezhouse.analytics.analytics_api.earthquakes.dto.EarthquakeFilter;
import com.vasquezhouse.analytics.analytics_api.earthquakes.dto.EarthquakeStats;
import com.vasquezhouse.analytics.analytics_api.earthquakes.dto.SortInput;
import com.vasquezhouse.analytics.analytics_api.earthquakes.model.Earthquake;
import com.vasquezhouse.analytics.analytics_api.relay.Connection;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class EarthquakeController {

    private final EarthquakeService earthquakeService;

    @QueryMapping
    public int earthquakesCount() {
        EarthquakeFilter noopFilter = new EarthquakeFilter();
        return (int) earthquakeService.getStats(noopFilter).getCount();
    }

    @QueryMapping
    public Connection<Earthquake> earthquakes(
        @Argument EarthquakeFilter filter,
        @Argument SortInput sort,
        @Argument Integer first,
        @Argument Integer last,
        @Argument String after,
        @Argument String before
    ) {
        return earthquakeService.getEarthquakes(filter, sort, first, last, after, before);
    }
    
    @QueryMapping
    public EarthquakeStats earthquakeStats(@Argument EarthquakeFilter filter) {
        return earthquakeService.getStats(filter);
    }
}
