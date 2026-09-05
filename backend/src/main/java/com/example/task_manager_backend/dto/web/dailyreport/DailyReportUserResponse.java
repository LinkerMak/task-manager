package com.example.task_manager_backend.dto.web.dailyreport;

import java.util.List;

public record DailyReportUserResponse(
        Long userId,
        String email,
        List<DailyReportTaskResponse> tasks
) {
}
