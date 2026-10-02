package org.example.scheduler.scheduler.outbox.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "scheduler.outbox.publishing")
public record OutboxPublishingProperties(
        Duration fixedDelay
) {
}