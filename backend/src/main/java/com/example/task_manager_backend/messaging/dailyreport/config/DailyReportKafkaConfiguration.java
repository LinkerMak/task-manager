package com.example.task_manager_backend.messaging.dailyreport.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.taskmanager.contracts.dailyreport.DailyReportGenerationRequest;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DailyReportKafkaConfiguration {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<
            String,
            DailyReportGenerationRequest
            > dailyReportKafkaListenerContainerFactory(
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

        JsonDeserializer<DailyReportGenerationRequest> valueDeserializer =
                new JsonDeserializer<>(
                        DailyReportGenerationRequest.class,
                        objectMapper,
                        false
                );

        valueDeserializer.addTrustedPackages(
                DailyReportGenerationRequest.class.getPackageName()
        );

        DefaultKafkaConsumerFactory<
                String,
                DailyReportGenerationRequest
                > consumerFactory =
                new DefaultKafkaConsumerFactory<>(
                        consumerProperties,
                        new StringDeserializer(),
                        valueDeserializer
                );

        ConcurrentKafkaListenerContainerFactory<
                String,
                DailyReportGenerationRequest
                > factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        return factory;
    }
}