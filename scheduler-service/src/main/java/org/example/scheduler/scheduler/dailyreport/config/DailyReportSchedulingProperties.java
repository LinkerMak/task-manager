package org.example.scheduler.scheduler.dailyreport.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "scheduler.daily-report-scheduling")
public record DailyReportSchedulingProperties(
        @NotBlank
        String cron,

        @NotBlank
        String zone
) {
}
