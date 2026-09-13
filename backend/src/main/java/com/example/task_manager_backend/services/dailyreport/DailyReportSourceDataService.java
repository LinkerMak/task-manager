package com.example.task_manager_backend.services.dailyreport;


import org.example.taskmanager.contracts.dailyreport.DailyReportUserTasksReady;

import java.time.OffsetDateTime;
import java.util.List;

public interface DailyReportSourceDataService {

    List<DailyReportUserTasksReady> getUsersWithTasks(
            OffsetDateTime periodStart,
            OffsetDateTime periodEnd
    );

}
