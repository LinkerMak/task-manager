package org.example.scheduler.config.kafka.dailyreport;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.taskmanager.contracts.dailyreport.DailyReportUserTasksReady;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DailyReportUserTasksReadyKafkaConfiguration {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<
            String,
            DailyReportUserTasksReady
            > dailyReportUserTasksReadyKafkaListenerContainerFactory(
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

        JsonDeserializer<DailyReportUserTasksReady> valueDeserializer =
                new JsonDeserializer<>(
                        DailyReportUserTasksReady.class,
                        objectMapper,
                        false
                );

        valueDeserializer.addTrustedPackages(
                DailyReportUserTasksReady.class.getPackageName()
        );

        DefaultKafkaConsumerFactory<
                String,
                DailyReportUserTasksReady
                > consumerFactory =
                new DefaultKafkaConsumerFactory<>(
                        consumerProperties,
                        new StringDeserializer(),
                        valueDeserializer
                );

        ConcurrentKafkaListenerContainerFactory<
                String,
                DailyReportUserTasksReady
                > factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        return factory;
    }
}