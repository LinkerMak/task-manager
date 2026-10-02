package org.example.taskmanager.contracts.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record TaskSnapshot(
        @NotNull
        Long id,

       @NotBlank()
       @Size(max = 255)
       String title,

       @Size(max = 10_000)
       String description,

       @NotNull
        TaskSnapshotStatus status,

       OffsetDateTime completedAt
) {
}
