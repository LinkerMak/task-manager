package com.example.task_manager_backend.security.config.dailyreport;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "spring.security.internal-api")
public record InternalApiProperties(
        @NotBlank
        String schedulerApiKey
) {
}
