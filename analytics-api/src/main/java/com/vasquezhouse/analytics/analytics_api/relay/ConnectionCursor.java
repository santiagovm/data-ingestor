package com.vasquezhouse.analytics.analytics_api.relay;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ConnectionCursor {
    public static String toCursor(Integer id) {
        return Base64.getEncoder().encodeToString(id.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static Integer fromCursor(String cursor) {
        String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
        return Integer.parseInt(decoded);
    }
}
