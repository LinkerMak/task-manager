package com.example.task_manager_backend.integration.kafka.registration;

import com.example.task_manager_backend.dto.web.security.RegisterRequest;
import com.example.task_manager_backend.integration.kafka.AbstractKafkaIntegrationTest;
import com.example.task_manager_backend.services.registration.UserRegisterService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.taskmanager.contracts.email.EmailSendingTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserRegistrationKafkaIntegrationTest
        extends AbstractKafkaIntegrationTest {

    private static final String EMAIL_SENDING_TASKS_TOPIC =
            "EMAIL_SENDING_TASKS";

    @Autowired
    private UserRegisterService userRegisterService;

    @Test
    void registerUserPublishesWelcomeEmailTask() {
        String email = "new-user-" + UUID.randomUUID() + "@example.com";
        String password = "Password1";

        try (Consumer<String, EmailSendingTask> consumer =
                     createEmailTasksConsumer()) {

            consumer.subscribe(
                    List.of(EMAIL_SENDING_TASKS_TOPIC)
            );

            consumer.poll(Duration.ofMillis(100));

            userRegisterService.register(
                    new RegisterRequest(
                            email,
                            password
                    )
            );

            ConsumerRecords<String, EmailSendingTask> records =
                    consumer.poll(Duration.ofSeconds(10));

            assertThat(records.count()).isEqualTo(1);

            EmailSendingTask task =
                    records.iterator()
                            .next()
                            .value();

            assertThat(task.messageId()).isNotNull();
            assertThat(task.recipientEmail()).isEqualTo(email);
            assertThat(task.subject())
                    .isEqualTo("Welcome to TaskManager");
            assertThat(task.body())
                    .contains("Welcome to Task Manager!");
        }
    }

    private Consumer<String, EmailSendingTask> createEmailTasksConsumer() {
        Map<String, Object> properties = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                KAFKA.getBootstrapServers(),

                ConsumerConfig.GROUP_ID_CONFIG,
                "welcome-email-test-" + UUID.randomUUID(),

                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest",

                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class,

                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class,

                JsonDeserializer.VALUE_DEFAULT_TYPE,
                EmailSendingTask.class.getName(),

                JsonDeserializer.TRUSTED_PACKAGES,
                EmailSendingTask.class.getPackageName(),

                JsonDeserializer.USE_TYPE_INFO_HEADERS,
                false
        );

        return new KafkaConsumer<>(properties);
    }
}