package com.vasquezhouse.batch.earthquakes.ingestion.manager.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

@Slf4j
@Configuration
public class RabbitConfig {
    
    @Value("${app.job-requests-exchange}")
    private String jobRequestsExchangeName;
    
    @Value("${app.job-requests-queue}")
    private String jobRequestsQueueName;
    
    @Value("${app.worker-requests-queue}")
    private String workerRequestsQueueName;

    @Value("${app.worker-replies-queue}")
    private String workerRepliesQueueName;
    
    @Bean
    public TopicExchange jobRequestsExchange() {
        return new TopicExchange(jobRequestsExchangeName);
    }

    @Bean
    public Queue jobRequestsQueue() {
        return new Queue(jobRequestsQueueName, true);
    }
    
    @Bean
    public Binding jobRequestsBinding() {
        return BindingBuilder.bind(jobRequestsQueue())
            .to(jobRequestsExchange())
            .with("");
    }
    
    @Bean
    public Queue workerRequestsQueue() {
        return new Queue(workerRequestsQueueName, true);
    }

    @Bean
    public Queue workerRepliesQueue() {
        return new Queue(workerRepliesQueueName, true);
    }

    @Bean
    public IntegrationFlow workerRequestsFlow(AmqpTemplate amqpTemplate) {
        return IntegrationFlow
            .from(requestsChannel())
            .handle(Amqp.outboundAdapter(amqpTemplate).routingKey(workerRequestsQueueName))
            .get();
    }

    @Bean
    public IntegrationFlow workerRepliesFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlow
            .from(Amqp.inboundAdapter(connectionFactory, workerRepliesQueueName))
            .channel(repliesChannel())
            .get();
    }

    @Bean
    public DirectChannel requestsChannel() {
        DirectChannel channel = new DirectChannel();
        channel.addInterceptor(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                log.debug(" >>> Sending partition to workers: {}", message.getPayload());
                return ChannelInterceptor.super.preSend(message, channel);
            }
        });

        return channel;
    }

    @Bean
    public DirectChannel repliesChannel() {
        return new DirectChannel();
    }
}
