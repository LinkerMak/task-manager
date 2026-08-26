package com.example.emailsender.messaging.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfiguration {

    public static final String EMAIL_SENDING_TASKS_TOPIC = "EMAIL_SENDING_TASKS";
    public static final int PARTITIONS_COUNT = 1;
    public static final int REPLICAS_COUNT = 1;

    public static final String EMAIL_SENDING_TASKS_DLT_TOPIC =
            EMAIL_SENDING_TASKS_TOPIC + ".DLT";

    @Bean
    public NewTopic emailSendingTasksTopic() {
        return TopicBuilder.name(EMAIL_SENDING_TASKS_TOPIC)
                .partitions(PARTITIONS_COUNT)
                .replicas(REPLICAS_COUNT)
                .build();
    }

    @Bean
    public NewTopic emailSendingTasksDltTopic() {
        return TopicBuilder.name(EMAIL_SENDING_TASKS_DLT_TOPIC)
                .partitions(PARTITIONS_COUNT)
                .replicas(REPLICAS_COUNT)
                .build();
    }
}
