package com.vasquezhouse.batch.job_mgmt_api.test_utils;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Properties;

public class PropertyReader {
    public static String getProperty(String key) {
        try {
            Properties properties = new Properties();
            properties.load(new ClassPathResource("application-test.properties").getInputStream());
            return properties.getProperty(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
