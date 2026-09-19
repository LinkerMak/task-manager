package com.example.emailsender.messaging.config;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaErrorHandlingConfiguration {

    private final KafkaTopicsProperties kafkaTopicsProperties;

    private static final long RETRY_INTERVAL_MS = 1_000L;
    private static final long MAX_RETRY_ATTEMPTS = 3L;

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            KafkaTemplate<Object, Object> kafkaTemplate
    ) {
        DeadLetterPublishingRecoverer recoverer =
                createRecoverer(kafkaTemplate);

        FixedBackOff backOff =
                createFixedBackOff();

        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(recoverer, backOff);

        errorHandler.addNotRetryableExceptions(
                ConstraintViolationException.class,
                SerializationException.class,
                DeserializationException.class
        );

        return errorHandler;
    }

    private FixedBackOff createFixedBackOff() {
        return new FixedBackOff(
                RETRY_INTERVAL_MS,
                MAX_RETRY_ATTEMPTS
        );
    }

    private DeadLetterPublishingRecoverer createRecoverer(
            KafkaTemplate<Object, Object> kafkaTemplate
    ) {
        return new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> {
                    log.error(
                            "Sending failed email task to DLT: topic={}, partition={}, offset={}",
                            record.topic(),
                            record.partition(),
                            record.offset(),
                            exception
                    );

                    return new TopicPartition(
                            kafkaTopicsProperties.emailSendingTasksDlt(),
                            record.partition()
                    );
                }
        );
    }
}
