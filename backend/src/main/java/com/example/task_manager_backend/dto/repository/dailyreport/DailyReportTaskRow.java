package com.example.task_manager_backend.dto.repository.dailyreport;

import com.example.task_manager_backend.models.task.TaskStatus;

import java.time.OffsetDateTime;

public record DailyReportTaskRow(
        Long userId,
        String email,
        Long taskId,
        String title,
        String description,
        TaskStatus status,
        OffsetDateTime completedAt
) {
}
