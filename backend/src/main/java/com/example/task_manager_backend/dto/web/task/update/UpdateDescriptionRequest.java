package com.example.task_manager_backend.dto.web.task.update;

import jakarta.validation.constraints.Size;

public record UpdateDescriptionRequest(
        @Size(max = 10_000, message = "Description must not exceed 10000 characters")
        String description
) {
}
