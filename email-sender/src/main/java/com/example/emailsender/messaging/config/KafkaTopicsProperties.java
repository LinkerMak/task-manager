package com.example.emailsender.messaging.config;

import org.example.taskmanager.contracts.email.topics.EmailSendingTopics;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka.topics")
public record KafkaTopicsProperties(
        String emailSendingTasks,
        String emailSendingTasksDlt
) {

    public KafkaTopicsProperties {
        if (emailSendingTasks == null || emailSendingTasks.isBlank()) {
            emailSendingTasks =
                    EmailSendingTopics.EMAIL_SENDING_TASKS;
        }

        if (emailSendingTasksDlt == null
                || emailSendingTasksDlt.isBlank()) {
            emailSendingTasksDlt =
                    EmailSendingTopics.EMAIL_SENDING_TASKS_DLT;
        }
    }
}