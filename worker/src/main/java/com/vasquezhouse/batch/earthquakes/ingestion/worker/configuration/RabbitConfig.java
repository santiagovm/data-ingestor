package com.vasquezhouse.batch.earthquakes.ingestion.worker.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
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

    @Value("${app.worker-requests-queue}")
    private String workerRequestsQueueName;

    @Value("${app.worker-replies-queue}")
    private String workerRepliesQueueName;

    @Bean
    public Queue workerRequestsQueue() {
        return new Queue(workerRequestsQueueName, true);
    }

    @Bean
    public Queue workerRepliesQueue() {
        return new Queue(workerRepliesQueueName, true);
    }

    @Bean
    public IntegrationFlow workerRequestsFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlow
            .from(Amqp.inboundAdapter(connectionFactory, workerRequestsQueueName))
            .channel(requestsChannel())
            .get();
    }

    @Bean
    public IntegrationFlow workerRepliesFlow(AmqpTemplate amqpTemplate) {
        return IntegrationFlow
            .from(repliesChannel())
            .handle(Amqp.outboundAdapter(amqpTemplate).routingKey(workerRepliesQueueName))
            .get();
    }

    @Bean
    public DirectChannel requestsChannel() {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel repliesChannel() {
        DirectChannel channel = new DirectChannel();
        channel.addInterceptor(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                log.debug(" >>> Sending reply to manager: {}", message.getPayload());
                return ChannelInterceptor.super.preSend(message, channel);
            }
        });

        return channel;
    }
}
