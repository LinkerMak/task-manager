package com.example.task_manager_backend.dto.web.dailyreport;

import com.example.task_manager_backend.models.task.TaskStatus;

import java.time.OffsetDateTime;

public record DailyReportTaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        OffsetDateTime completedAt
) {
}
