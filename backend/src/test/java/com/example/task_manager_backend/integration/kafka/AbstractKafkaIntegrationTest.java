package com.example.task_manager_backend.integration.kafka;

import com.example.task_manager_backend.integration.AbstractDatabaseIntegrationTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractKafkaIntegrationTest
        extends AbstractDatabaseIntegrationTest {

    protected static final KafkaContainer KAFKA =
            new KafkaContainer(
                    DockerImageName.parse(
                            "apache/kafka:3.8.1"
                    )
            );

    static {
        KAFKA.start();
    }

    @DynamicPropertySource
    static void registerKafkaProperties(
            DynamicPropertyRegistry registry
    ) {
        registry.add(
                "spring.kafka.bootstrap-servers",
                KAFKA::getBootstrapServers
        );
    }
}