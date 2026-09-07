package org.example.taskmanager.contracts.dailyreport;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record DailyReportSourceDataRequest(
        @NotNull
        OffsetDateTime periodStart,
        @NotNull
        OffsetDateTime periodEnd
) {
}
