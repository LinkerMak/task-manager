package org.example.taskmanager.contracts.dailyreport;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.taskmanager.contracts.validation.period.range.PeriodRange;
import org.example.taskmanager.contracts.validation.period.range.ValidPeriodRange;

import java.time.OffsetDateTime;
import java.util.List;

@ValidPeriodRange
public record DailyReportSourceDataResponse(
        @NotNull
        OffsetDateTime periodStart,

        @NotNull
        OffsetDateTime periodEnd,

        @NotNull
        List<@NotNull @Valid DailyReportUserData> users
) implements PeriodRange {
}
