package com.example.task_manager_backend.dto.web.task;

import com.example.task_manager_backend.models.task.TaskStatus;

import java.time.OffsetDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus taskStatus,
        OffsetDateTime completedAt
) {
}
