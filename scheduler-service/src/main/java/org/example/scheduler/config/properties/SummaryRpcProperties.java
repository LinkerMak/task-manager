package org.example.scheduler.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "scheduler.summary")
public record SummaryRpcProperties(
        String replyGroupId,
        Duration replyTimeout
) {
}
