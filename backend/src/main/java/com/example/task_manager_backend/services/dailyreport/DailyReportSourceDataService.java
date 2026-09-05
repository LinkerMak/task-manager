package com.example.task_manager_backend.services.dailyreport;

import com.example.task_manager_backend.dto.web.dailyreport.DailyReportSourceDateResponse;

import java.time.OffsetDateTime;

public interface DailyReportSourceDataService {

    DailyReportSourceDateResponse getDataSource(
            OffsetDateTime periodStart,
            OffsetDateTime periodEnd
    );

}
