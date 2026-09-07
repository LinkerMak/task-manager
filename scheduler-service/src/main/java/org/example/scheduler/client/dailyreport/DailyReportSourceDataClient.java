package org.example.scheduler.client.dailyreport;

import org.example.taskmanager.contracts.dailyreport.DailyReportSourceDataResponse;

import java.time.OffsetDateTime;

public interface DailyReportSourceDataClient {

    DailyReportSourceDataResponse getSourceData(
            OffsetDateTime periodStart,
            OffsetDateTime periodEnd
    );


}
