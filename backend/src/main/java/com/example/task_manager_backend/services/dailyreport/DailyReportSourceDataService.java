package com.example.task_manager_backend.services.dailyreport;

import com.example.task_manager_backend.dto.web.dailyreport.DailyReportSourceDataResponse;

import java.time.OffsetDateTime;

public interface DailyReportSourceDataService {

    DailyReportSourceDataResponse getDataSource(
            OffsetDateTime periodStart,
            OffsetDateTime periodEnd
    );

}
