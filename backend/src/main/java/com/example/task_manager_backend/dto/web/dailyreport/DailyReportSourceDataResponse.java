package com.example.task_manager_backend.dto.web.dailyreport;

import java.time.OffsetDateTime;
import java.util.List;

public record DailyReportSourceDataResponse(
        OffsetDateTime periodStart,
        OffsetDateTime periodEnd,
        List<DailyReportUserResponse> userResponses
) {
}
