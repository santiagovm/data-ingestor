package com.vasquezhouse.analytics.analytics_api.earthquakes.dto;

import lombok.Data;

@Data
public class SortInput {
    private SortField field;
    private SortDirection direction;
}
