package org.example.scheduler.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.taskmanager.contracts.summary.TaskSummaryRequest;
import org.example.taskmanager.contracts.summary.TaskSummaryResponse;
import org.example.taskmanager.contracts.summary.TaskSummaryTopics;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaRequestReplyConfiguration {

    @Bean
    ConsumerFactory<String, TaskSummaryResponse> taskSummaryReplyConsumerFactory(
            KafkaProperties kafkaProperties
    ) {
        Map<String, Object> properties = new HashMap<>(
                kafkaProperties.buildConsumerProperties()
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
                TaskSummaryResponse.class.getName()
        );
        properties.put(
                JsonDeserializer.USE_TYPE_INFO_HEADERS,
                false
        );
        properties.put(
                JsonDeserializer.TRUSTED_PACKAGES,
                TaskSummaryResponse.class.getPackageName()
        );

        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    ConcurrentMessageListenerContainer<String, TaskSummaryResponse> taskSummaryReplayContainer(
            ConsumerFactory<String, TaskSummaryResponse> taskSummaryReplyConsumerFactory,
            SummaryRpcProperties properties
    ) {
        ConcurrentMessageListenerContainer<String, TaskSummaryResponse> container =
                new ConcurrentMessageListenerContainer<>(
                        taskSummaryReplyConsumerFactory,
                        new ContainerProperties(TaskSummaryTopics.TASK_SUMMARY_REPLIES)
                );

        container.getContainerProperties().setGroupId(properties.replyGroupId());

        container.setAutoStartup(false);

        return container;
    }

    @Bean
    ReplyingKafkaTemplate<String, TaskSummaryRequest, TaskSummaryResponse> taskSummaryReplyingKafkaTemplate(
            ProducerFactory<String, TaskSummaryRequest> producerFactory,
            ConcurrentMessageListenerContainer<String, TaskSummaryResponse> taskSummaryReplyContainer
    ) {
        return new ReplyingKafkaTemplate<>(
                producerFactory,
                taskSummaryReplyContainer
        );
    }
}
