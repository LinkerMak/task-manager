package org.example.scheduler.config;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "scheduler.daily-report-source-rpc")
public record DailyReportSourceRpcProperties(
    @NotBlank
    String replyGroupId,

    @NotNull
    Duration replyTimeout
)
{
}
