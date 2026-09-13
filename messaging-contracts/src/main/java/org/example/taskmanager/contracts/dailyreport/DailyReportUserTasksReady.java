package org.example.taskmanager.contracts.dailyreport;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.example.taskmanager.contracts.task.TaskSnapshot;
import org.example.taskmanager.contracts.validation.period.range.PeriodRange;
import org.example.taskmanager.contracts.validation.period.range.ValidPeriodRange;

import java.time.OffsetDateTime;
import java.util.List;

@ValidPeriodRange
public record DailyReportUserTasksReady(
        @NotNull
        Long userId,

        @NotNull
        @Email
        String email,

        @NotNull
        OffsetDateTime periodStart,

        @NotNull
        OffsetDateTime periodEnd,

        @NotEmpty
        List<@NotNull @Valid TaskSnapshot> tasks
) implements PeriodRange {
}
