package com.vasquezhouse.analytics.analytics_api.relay;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Connection<T> {
    private List<Edge<T>> edges;
    private PageInfo pageInfo;
    private long totalCount;
}
