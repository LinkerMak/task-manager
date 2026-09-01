package org.example.summarizationservice.messaging.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.example.summarizationservice.llm.deepseek.exceptions.nonretryable.NonRetryableDeepSeekException;
import org.example.taskmanager.contracts.summary.TaskSummaryTopics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaErrorHandlingConfiguration {

    private static final long RETRY_BACKOFF_MILLIS = 2_000L;
    private static final long MAX_RETRY_ATTEMPTS = 3L;

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        DeadLetterPublishingRecoverer recoverer =
                buildDeadLetterRecoverer(kafkaTemplate);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(RETRY_BACKOFF_MILLIS, MAX_RETRY_ATTEMPTS)
        );

        errorHandler.addNotRetryableExceptions(
                NonRetryableDeepSeekException.class,
                IllegalArgumentException.class,
                MethodArgumentNotValidException.class,
                DeserializationException.class,
                MessageConversionException.class
        );

        return errorHandler;
    }

    private DeadLetterPublishingRecoverer buildDeadLetterRecoverer(KafkaTemplate<String, Object> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> {
                    log.error(
                            "Sending task summary request to DLT: "
                                    + "topic={}, partition={}, offset={}, key={}",
                            record.topic(),
                            record.partition(),
                            record.offset(),
                            record.key(),
                            exception
                    );

                    return new TopicPartition(
                            TaskSummaryTopics.TASK_SUMMARY_REQUESTS_DLT,
                            record.partition()
                    );
                }
        );
    }
}
