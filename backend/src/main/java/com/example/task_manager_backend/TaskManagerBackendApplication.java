package com.example.task_manager_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class TaskManagerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerBackendApplication.class, args);
    }

}
