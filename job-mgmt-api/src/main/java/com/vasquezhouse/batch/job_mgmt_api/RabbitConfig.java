package com.vasquezhouse.batch.job_mgmt_api;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    
    @Value("${app.jobs-exchange}")
    private String exchangeName;
    
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }
}
