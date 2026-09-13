package com.example.task_manager_backend.messaging.dailyreport.producer;

import org.example.taskmanager.contracts.dailyreport.DailyReportUserTasksReady;

public interface DailyReportUserTasksReadyProducer {
    void publish(DailyReportUserTasksReady message);
}
