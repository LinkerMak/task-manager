package org.example.taskmanager.contracts.summary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TaskSummaryResponse(
        @NotNull
        UUID requestId,

        @NotBlank
        String summaryText
) {
}
