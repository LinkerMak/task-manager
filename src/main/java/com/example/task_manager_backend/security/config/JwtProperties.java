package com.example.task_manager_backend.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "spring.security.jwt")
public record JwtProperties(
        String secret,
        Duration accessTokenTtl
) {

}
