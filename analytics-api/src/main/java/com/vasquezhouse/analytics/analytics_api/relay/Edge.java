package com.vasquezhouse.analytics.analytics_api.relay;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Edge<T> {
    private String cursor;
    private T node;
}
