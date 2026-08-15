package com.example.task_manager_backend.dto.web.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(

        @NotBlank(message = "Title must not be blank")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        @Size(max = 10_000, message = "Description must not exceed 10000 characters")
        String description
) {
}
