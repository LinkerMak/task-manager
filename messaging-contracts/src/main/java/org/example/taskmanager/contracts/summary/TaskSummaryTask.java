package org.example.taskmanager.contracts.summary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record TaskSummaryTask(
        @NotNull
        Long id,

       @NotBlank()
       @Size(max = 255)
       String title,

       @Size(max = 10_000)
       String description,

       @NotNull
       TaskSummaryTaskStatus status,

       OffsetDateTime completedAt
) {
}
