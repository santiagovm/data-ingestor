package com.vasquezhouse.batch.job_mgmt_api.test_utils;

import org.springframework.graphql.client.HttpGraphQlClient;

public class AnalyticsService {
    
    private static final String EARTHQUAKES_COUNT_QUERY = """
        query {
            earthquakesCount
        }
        """;
    
    public int getEarthquakesCount() {
        String apiUrl = PropertyReader.getProperty("test.analytics-api-url");
        
        return HttpGraphQlClient.builder()
            .webClient(builder -> builder.baseUrl(apiUrl + "/graphql").build())
            .build()
            .document(EARTHQUAKES_COUNT_QUERY)
            .retrieve("earthquakesCount")
            .toEntity(Integer.class)
            .block();
    }
}
