package com.example.emailsender.integration.messaging.consumer;

import com.example.emailsender.integration.messaging.AbstractKafkaDatabaseIntegrationTest;
import com.example.emailsender.messaging.config.KafkaTopicsProperties;
import com.example.emailsender.persistence.entity.EmailDelivery;
import com.example.emailsender.persistence.entity.EmailDeliveryStatus;
import com.example.emailsender.repositories.EmailDeliveryRepository;
import com.example.emailsender.services.EmailSendingService;
import org.example.taskmanager.contracts.email.EmailSendingTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@TestPropertySource(
        properties = {
                "spring.kafka.consumer.group-id=email-sender-kafka-it",
                "spring.kafka.topics.email-sending-tasks=EMAIL_SENDING_TASKS_KAFKA_IT",
                "spring.kafka.topics.email-sending-tasks-dlt=EMAIL_SENDING_TASKS_KAFKA_IT.DLT"
        }
)
@DirtiesContext(
        classMode = DirtiesContext.ClassMode.AFTER_CLASS
)
class EmailSendingTaskKafkaIntegrationTest
        extends AbstractKafkaDatabaseIntegrationTest {

    private static final Duration KAFKA_SEND_TIMEOUT =
            Duration.ofSeconds(10);

    private static final Duration EVENT_PROCESSING_TIMEOUT =
            Duration.ofSeconds(20);

    private static final Duration DUPLICATE_PROCESSING_WINDOW =
            Duration.ofSeconds(2);

    @Autowired
    private KafkaTopicsProperties kafkaTopicsProperties;

    @Autowired
    private KafkaTemplate<String, EmailSendingTask> kafkaTemplate;

    @Autowired
    private EmailDeliveryRepository emailDeliveryRepository;

    @MockitoBean
    private EmailSendingService emailSendingService;

    @BeforeEach
    void setUp() {
        emailDeliveryRepository.deleteAll();
        reset(emailSendingService);
    }

    @Test
    void shouldSendNewEmailTaskAndPersistSentDelivery()
            throws Exception {

        EmailSendingTask task = newEmailSendingTask();

        send(task);

        await()
                .atMost(EVENT_PROCESSING_TIMEOUT)
                .untilAsserted(() -> {
                    EmailDelivery delivery = emailDeliveryRepository
                            .findById(task.messageId())
                            .orElseThrow();

                    assertThat(delivery.getMessageId())
                            .isEqualTo(task.messageId());

                    assertThat(delivery.getRecipientEmail())
                            .isEqualTo(task.recipientEmail());

                    assertThat(delivery.getSubject())
                            .isEqualTo(task.subject());

                    assertThat(delivery.getStatus())
                            .isEqualTo(EmailDeliveryStatus.SENT);

                    assertThat(delivery.getSentAt())
                            .isNotNull();
                });

        verify(emailSendingService)
                .send(task);
    }

    @Test
    void shouldNotSendDuplicateTaskWhenDeliveryIsAlreadySent()
            throws Exception {

        EmailSendingTask task = newEmailSendingTask();

        send(task);

        await()
                .atMost(EVENT_PROCESSING_TIMEOUT)
                .untilAsserted(() -> {
                    EmailDelivery delivery = emailDeliveryRepository
                            .findById(task.messageId())
                            .orElseThrow();

                    assertThat(delivery.getStatus())
                            .isEqualTo(EmailDeliveryStatus.SENT);
                });

        verify(emailSendingService)
                .send(task);

        clearInvocations(emailSendingService);

        send(task);

        await()
                .during(DUPLICATE_PROCESSING_WINDOW)
                .atMost(
                        DUPLICATE_PROCESSING_WINDOW.plusSeconds(1)
                )
                .untilAsserted(() ->
                        verify(
                                emailSendingService,
                                never()
                        ).send(task)
                );

        EmailDelivery delivery = emailDeliveryRepository
                .findById(task.messageId())
                .orElseThrow();

        assertThat(delivery.getStatus())
                .isEqualTo(EmailDeliveryStatus.SENT);

        assertThat(delivery.getSentAt())
                .isNotNull();
    }

    @Test
    void shouldRetryPreviouslyFailedDeliveryAndMarkItAsSent()
            throws Exception {

        EmailSendingTask task = newEmailSendingTask();

        EmailDelivery failedDelivery = new EmailDelivery(
                task.messageId(),
                task.recipientEmail(),
                task.subject()
        );

        failedDelivery.markAsFailed();

        emailDeliveryRepository.saveAndFlush(failedDelivery);

        send(task);

        await()
                .atMost(EVENT_PROCESSING_TIMEOUT)
                .untilAsserted(() -> {
                    EmailDelivery delivery = emailDeliveryRepository
                            .findById(task.messageId())
                            .orElseThrow();

                    assertThat(delivery.getMessageId())
                            .isEqualTo(task.messageId());

                    assertThat(delivery.getRecipientEmail())
                            .isEqualTo(task.recipientEmail());

                    assertThat(delivery.getSubject())
                            .isEqualTo(task.subject());

                    assertThat(delivery.getStatus())
                            .isEqualTo(EmailDeliveryStatus.SENT);

                    assertThat(delivery.getSentAt())
                            .isNotNull();
                });

        verify(emailSendingService)
                .send(task);
    }

    private void send(EmailSendingTask task)
            throws Exception {

        kafkaTemplate.send(
                        kafkaTopicsProperties.emailSendingTasks(),
                        task.messageId().toString(),
                        task
                )
                .get(
                        KAFKA_SEND_TIMEOUT.toSeconds(),
                        TimeUnit.SECONDS
                );
    }

    private EmailSendingTask newEmailSendingTask() {
        return new EmailSendingTask(
                UUID.randomUUID(),
                "user@example.com",
                "Welcome to TaskManager",
                "Welcome to Task Manager!"
        );
    }
}