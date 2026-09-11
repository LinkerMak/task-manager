package org.example.taskmanager.contracts.summary;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.example.taskmanager.contracts.validation.period.range.PeriodRange;
import org.example.taskmanager.contracts.validation.period.range.ValidPeriodRange;
import org.example.taskmanager.contracts.task.TaskSnapshot;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@ValidPeriodRange
public record TaskSummaryRequest(
        @NotNull
        UUID requestId,

        @NotNull
        OffsetDateTime periodStart,

        @NotNull
        OffsetDateTime periodEnd,

        @NotEmpty
        List<@NotNull @Valid TaskSnapshot> tasks
) implements PeriodRange {
}
