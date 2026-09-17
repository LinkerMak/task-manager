package com.example.emailsender.messaging.consumer;

import com.example.emailsender.messaging.config.KafkaTopicConfiguration;
import com.example.emailsender.services.EmailDeliveryProcessingService;
import org.example.taskmanager.contracts.email.EmailSendingTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = {
                KafkaTopicConfiguration.EMAIL_SENDING_TASKS_TOPIC,
                KafkaTopicConfiguration.EMAIL_SENDING_TASKS_DLT_TOPIC
        }
)
class EmailSendingTaskKafkaIntegrationTest {

    private static final Duration KAFKA_SEND_TIMEOUT =
            Duration.ofSeconds(10);

    @Autowired
    private KafkaTemplate<String, EmailSendingTask> kafkaTemplate;

    @MockitoBean
    private EmailDeliveryProcessingService emailDeliveryProcessingService;

    @DynamicPropertySource
    static void configureKafka(
            DynamicPropertyRegistry registry
    ) {
        registry.add(
                "spring.kafka.bootstrap-servers",
                () -> System.getProperty(
                        "spring.embedded.kafka.brokers"
                )
        );
    }

    @Test
    void emailTaskFromKafkaIsDelegatedToProcessingService()
            throws Exception {

        EmailSendingTask task = new EmailSendingTask(
                UUID.randomUUID(),
                "user@example.com",
                "Welcome to TaskManager",
                "Welcome to Task Manager!"
        );

        kafkaTemplate.send(
                        KafkaTopicConfiguration.EMAIL_SENDING_TASKS_TOPIC,
                        task.messageId().toString(),
                        task
                )
                .get(
                        KAFKA_SEND_TIMEOUT.toSeconds(),
                        TimeUnit.SECONDS
                );

        verify(
                emailDeliveryProcessingService,
                timeout(10_000)
        ).process(
                argThat(receivedTask ->
                        receivedTask != null
                                && receivedTask.messageId()
                                .equals(task.messageId())
                                && receivedTask.recipientEmail()
                                .equals(task.recipientEmail())
                                && receivedTask.subject()
                                .equals(task.subject())
                                && receivedTask.body()
                                .equals(task.body())
                )
        );
    }
}