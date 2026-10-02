package com.example.emailsender.messaging.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfiguration {

    private static final int PARTITIONS_COUNT = 1;
    private static final int REPLICAS_COUNT = 1;

    private final KafkaTopicsProperties kafkaTopicsProperties;

    @Bean
    public NewTopic emailSendingTasksTopic() {
        return TopicBuilder
                .name(kafkaTopicsProperties.emailSendingTasks())
                .partitions(PARTITIONS_COUNT)
                .replicas(REPLICAS_COUNT)
                .build();
    }

    @Bean
    public NewTopic emailSendingTasksDltTopic() {
        return TopicBuilder
                .name(kafkaTopicsProperties.emailSendingTasksDlt())
                .partitions(PARTITIONS_COUNT)
                .replicas(REPLICAS_COUNT)
                .build();
    }
}