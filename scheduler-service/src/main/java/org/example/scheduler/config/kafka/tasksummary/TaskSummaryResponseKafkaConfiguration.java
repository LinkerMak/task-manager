package org.example.scheduler.config.kafka.tasksummary;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.taskmanager.contracts.summary.TaskSummaryResponse;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TaskSummaryResponseKafkaConfiguration {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<
            String,
            TaskSummaryResponse
            > taskSummaryResponseKafkaListenerContainerFactory(
            KafkaProperties kafkaProperties,
            ObjectMapper objectMapper
    ) {
        Map<String, Object> consumerProperties = new HashMap<>(
                kafkaProperties.buildConsumerProperties(null)
        );

        consumerProperties.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        JsonDeserializer<TaskSummaryResponse> valueDeserializer =
                new JsonDeserializer<>(
                        TaskSummaryResponse.class,
                        objectMapper,
                        false
                );

        valueDeserializer.addTrustedPackages(
                TaskSummaryResponse.class.getPackageName()
        );

        DefaultKafkaConsumerFactory<String, TaskSummaryResponse>
                consumerFactory =
                new DefaultKafkaConsumerFactory<>(
                        consumerProperties,
                        new StringDeserializer(),
                        valueDeserializer
                );

        ConcurrentKafkaListenerContainerFactory<String, TaskSummaryResponse>
                factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        return factory;
    }
}