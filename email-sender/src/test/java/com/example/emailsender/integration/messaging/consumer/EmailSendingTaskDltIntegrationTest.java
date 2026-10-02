package com.example.emailsender.integration.messaging.consumer;

import com.example.emailsender.integration.messaging.AbstractKafkaDatabaseIntegrationTest;
import com.example.emailsender.integration.messaging.KafkaDltTestConsumer;
import com.example.emailsender.messaging.config.KafkaTopicsProperties;
import com.example.emailsender.persistence.entity.EmailDelivery;
import com.example.emailsender.persistence.entity.EmailDeliveryStatus;
import com.example.emailsender.repositories.EmailDeliveryRepository;
import com.example.emailsender.services.EmailSendingService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.taskmanager.contracts.email.EmailSendingTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.MailSendException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@TestPropertySource(
        properties = {
                "spring.kafka.consumer.group-id=email-sender-dlt-it",
                "spring.kafka.topics.email-sending-tasks=EMAIL_SENDING_TASKS_DLT_IT",
                "spring.kafka.topics.email-sending-tasks-dlt=EMAIL_SENDING_TASKS_DLT_IT.DLT"
        }
)
@DirtiesContext(
        classMode = DirtiesContext.ClassMode.AFTER_CLASS
)
class EmailSendingTaskDltIntegrationTest
        extends AbstractKafkaDatabaseIntegrationTest {

    private static final Duration KAFKA_SEND_TIMEOUT =
            Duration.ofSeconds(10);

    private static final Duration EVENT_PROCESSING_TIMEOUT =
            Duration.ofSeconds(20);

    private static final int TOTAL_SENDING_ATTEMPTS = 4;

    @Autowired
    private KafkaTopicsProperties kafkaTopicsProperties;

    @Autowired
    private KafkaTemplate<String, EmailSendingTask> kafkaTemplate;

    @Autowired
    private EmailDeliveryRepository emailDeliveryRepository;

    @Autowired
    private KafkaProperties kafkaProperties;

    @MockitoBean(name = "smtpEmailSendingService")
    private EmailSendingService emailSendingService;

    @BeforeEach
    void setUp() {
        emailDeliveryRepository.deleteAll();
        reset(emailSendingService);
    }

    @Test
    void shouldMarkDeliveryAsFailedRetryThreeTimesAndPublishTaskToDlt()
            throws Exception {

        EmailSendingTask task = newEmailSendingTask();

        MailSendException mailSendException =
                new MailSendException(
                        "SMTP server is unavailable"
                );

        doThrow(mailSendException)
                .when(emailSendingService)
                .send(argThat(
                        actualTask -> actualTask != null
                                && actualTask.messageId()
                                .equals(task.messageId())
                ));

        KafkaDltTestConsumer dltTestConsumer =
                new KafkaDltTestConsumer(
                        kafkaTopicsProperties.emailSendingTasksDlt(),
                        kafkaProperties
                );

        try (
                Consumer<String, EmailSendingTask> dltConsumer =
                        dltTestConsumer.createConsumer()
        ) {
            dltConsumer.subscribe(
                    List.of(
                            kafkaTopicsProperties.emailSendingTasksDlt()
                    )
            );

            send(task);

            await()
                    .atMost(EVENT_PROCESSING_TIMEOUT)
                    .untilAsserted(() -> {
                        var delivery = emailDeliveryRepository.findById(task.messageId());

                        assertThat(delivery)
                                .as(
                                        "EmailDelivery должна существовать: messageId=%s",
                                        task.messageId()
                                )
                                .isPresent();

                        EmailDelivery actualDelivery = delivery.orElseThrow();

                        assertThat(actualDelivery.getStatus())
                                .isEqualTo(EmailDeliveryStatus.FAILED);

                        assertThat(actualDelivery.getSentAt())
                                .isNull();
                    });

            await()
                    .atMost(EVENT_PROCESSING_TIMEOUT)
                    .untilAsserted(() ->
                            verify(
                                    emailSendingService,
                                    times(TOTAL_SENDING_ATTEMPTS)
                            ).send(task)
                    );

            ConsumerRecord<String, EmailSendingTask> dltRecord =
                    dltTestConsumer.awaitRecord(
                            dltConsumer,
                            task.messageId()
                    );

            assertThat(dltRecord.key())
                    .isEqualTo(task.messageId().toString());

            assertThat(dltRecord.topic())
                    .isEqualTo(
                            kafkaTopicsProperties.emailSendingTasksDlt()
                    );

            assertThat(dltRecord.value())
                    .isNotNull();

            assertThat(dltRecord.value().messageId())
                    .isEqualTo(task.messageId());

            assertThat(dltRecord.value().recipientEmail())
                    .isEqualTo(task.recipientEmail());

            assertThat(dltRecord.value().subject())
                    .isEqualTo(task.subject());

            assertThat(dltRecord.value().body())
                    .isEqualTo(task.body());
        }
    }

    private void send(EmailSendingTask task) throws Exception {
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