package com.vasquezhouse.analytics.analytics_api.configuration;

import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfig {
    
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        GraphQLScalarType dateTimeScalar = GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("DateTime scalar")
            .coercing(new GraphQLDateTimeCoercing())
            .build();
        
        return wiringBuilder -> wiringBuilder.scalar(dateTimeScalar);
    }
}
