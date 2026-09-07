package com.example.task_manager_backend.messaging.dailyreport.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.taskmanager.contracts.dailyreport.DailyReportSourceDataRequest;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DailyReportKafkaConfiguration {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<
            String,
            DailyReportSourceDataRequest
            > dailyReportKafkaListenerContainerFactory(
            KafkaProperties kafkaProperties,
            JsonMapper jsonMapper,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        Map<String, Object> consumerProperties = new HashMap<>(
                kafkaProperties.buildConsumerProperties()
        );

        consumerProperties.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        JacksonJsonDeserializer<DailyReportSourceDataRequest>
                valueDeserializer =
                new JacksonJsonDeserializer<DailyReportSourceDataRequest>(
                        DailyReportSourceDataRequest.class,
                        jsonMapper,
                        false
                );

        valueDeserializer.addTrustedPackages(
                DailyReportSourceDataRequest.class.getPackageName()
        );

        DefaultKafkaConsumerFactory<
                String,
                DailyReportSourceDataRequest
                > consumerFactory =
                new DefaultKafkaConsumerFactory<>(
                        consumerProperties,
                        new StringDeserializer(),
                        valueDeserializer
                );

        ConcurrentKafkaListenerContainerFactory<
                String,
                DailyReportSourceDataRequest
                > factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setReplyTemplate(kafkaTemplate);

        return factory;
    }
}