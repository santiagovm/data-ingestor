package com.vasquezhouse.analytics.analytics_api.relay;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageInfo {
    private boolean hasNextPage;
    private boolean hasPreviousPage;
    private String startCursor;
    private String endCursor;
}
