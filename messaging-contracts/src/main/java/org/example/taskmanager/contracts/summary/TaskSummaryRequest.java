package org.example.taskmanager.contracts.summary;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.example.taskmanager.contracts.summary.validation.ValidSummaryPeriod;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@ValidSummaryPeriod
public record TaskSummaryRequest(
        @NotNull
        UUID requestId,

        @NotNull
        OffsetDateTime periodStart,

        @NotNull
        OffsetDateTime periodEnd,

        @NotEmpty
        List<@NotNull @Valid TaskSummaryTask> tasks
) {
}
