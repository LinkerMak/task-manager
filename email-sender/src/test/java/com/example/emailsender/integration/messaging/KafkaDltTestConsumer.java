package com.example.emailsender.integration.messaging;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.taskmanager.contracts.email.EmailSendingTask;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.awaitility.Awaitility.await;

public class KafkaDltTestConsumer {

    private static final Duration DLT_RECEIVE_TIMEOUT =
            Duration.ofSeconds(20);

    private static final Duration POLL_TIMEOUT =
            Duration.ofMillis(500);

    private static final String TRUSTED_PACKAGES =
            "org.example.taskmanager.contracts.email";

    private final String dltTopic;
    private final KafkaProperties kafkaProperties;

    public KafkaDltTestConsumer(
            String dltTopic,
            KafkaProperties kafkaProperties
    ) {
        this.dltTopic = dltTopic;
        this.kafkaProperties = kafkaProperties;
    }

    public Consumer<String, EmailSendingTask> createConsumer() {
        Map<String, Object> properties = new HashMap<>(
                kafkaProperties.buildConsumerProperties()
        );

        properties.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "dlt-test-consumer-" + UUID.randomUUID()
        );

        properties.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        properties.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        properties.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class
        );

        properties.put(
                JsonDeserializer.VALUE_DEFAULT_TYPE,
                EmailSendingTask.class.getName()
        );

        properties.put(
                JsonDeserializer.TRUSTED_PACKAGES,
                TRUSTED_PACKAGES
        );

        ConsumerFactory<String, EmailSendingTask> consumerFactory =
                new DefaultKafkaConsumerFactory<>(properties);

        return consumerFactory.createConsumer();
    }

    public ConsumerRecord<String, EmailSendingTask> awaitRecord(
            Consumer<String, EmailSendingTask> consumer,
            UUID messageId
    ) {
        return await()
                .atMost(DLT_RECEIVE_TIMEOUT)
                .until(
                        () -> pollRecord(consumer, messageId),
                        Objects::nonNull
                );
    }

    private ConsumerRecord<String, EmailSendingTask> pollRecord(
            Consumer<String, EmailSendingTask> consumer,
            UUID messageId
    ) {
        Iterable<ConsumerRecord<String, EmailSendingTask>> records =
                consumer.poll(POLL_TIMEOUT)
                        .records(dltTopic);

        for (ConsumerRecord<String, EmailSendingTask> record : records) {
            EmailSendingTask task = record.value();

            if (task != null
                    && messageId.equals(task.messageId())) {
                return record;
            }
        }

        return null;
    }
}