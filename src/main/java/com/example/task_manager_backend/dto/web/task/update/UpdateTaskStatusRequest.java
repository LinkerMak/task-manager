package com.example.task_manager_backend.dto.web.task.update;

import com.example.task_manager_backend.models.task.TaskStatus;
import jakarta.persistence.Enumerated;

public record UpdateTaskStatusRequest(
        @Enumerated
        TaskStatus status
) {
}
