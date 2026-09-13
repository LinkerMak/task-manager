package org.example.scheduler.producer.dailyreport;

import org.example.taskmanager.contracts.dailyreport.DailyReportGenerationRequest;

public interface DailyReportGenerationRequestProducer {

    void publish(DailyReportGenerationRequest request);
}
