package org.example.taskmanager.contracts.dailyreport;

import jakarta.validation.constraints.NotNull;
import org.example.taskmanager.contracts.validation.period.range.PeriodRange;
import org.example.taskmanager.contracts.validation.period.range.ValidPeriodRange;

import java.time.OffsetDateTime;

@ValidPeriodRange
public record DailyReportSourceDataRequest(
        @NotNull
        OffsetDateTime periodStart,
        @NotNull
        OffsetDateTime periodEnd
) implements PeriodRange {
}
