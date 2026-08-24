package com.example.task_manager_backend.dto.web.task.update;

import com.example.task_manager_backend.models.task.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(
        @NotNull(message = "Status must not be null")
        TaskStatus status
) {
}
