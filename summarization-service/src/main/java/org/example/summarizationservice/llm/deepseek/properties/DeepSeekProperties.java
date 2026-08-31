package org.example.summarizationservice.llm.deepseek.properties;

import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "llm.deepseek")
public record DeepSeekProperties(

        @NotBlank
        String baseUrl,

        @NotBlank
        String apiKey,

        @NotBlank
        String model,

        @NotNull
        Duration connectionTimeout,

        @NotNull
        Duration responseTimeout,

        @DecimalMin("0.0")
        @DecimalMax("2.0")
        double temperature,

        @Positive
        int maxOutputTokens
) {
}
