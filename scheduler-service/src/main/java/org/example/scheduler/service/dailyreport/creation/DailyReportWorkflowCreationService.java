package org.example.scheduler.service.dailyreport.creation;

import org.example.taskmanager.contracts.dailyreport.DailyReportUserTasksReady;
import org.springframework.transaction.annotation.Transactional;

public interface DailyReportWorkflowCreationService {
    @Transactional
    void createIfAbsent(
            DailyReportUserTasksReady message
    );
}
