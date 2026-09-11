package com.example.task_manager_backend.messaging.dailyreport.listener;

import com.example.task_manager_backend.services.dailyreport.DailyReportSourceDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanager.contracts.dailyreport.DailyReportSourceDataRequest;
import org.example.taskmanager.contracts.dailyreport.DailyReportSourceDataResponse;
import org.example.taskmanager.contracts.dailyreport.topics.DailyReportTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyReportSourceDataRequestListener {

    private static final String GROUP_ID = "task-manager-backend.daily-report-source-data";

    private final DailyReportSourceDataService dailyReportSourceDataService;

    @KafkaListener(
            topics = DailyReportTopics.SOURCE_DATA_REQUEST,
            groupId = GROUP_ID,
            containerFactory = "dailyReportKafkaListenerContainerFactory"
    )
    @SendTo
    public DailyReportSourceDataResponse handle(
            @Valid @Payload DailyReportSourceDataRequest request
    ) {
        log.info(
                "Received daily report source data request: periodStart={}, periodEnd={}",
                request.periodStart(),
                request.periodEnd()
        );

        DailyReportSourceDataResponse response =
                dailyReportSourceDataService.getDataSource(
                        request.periodStart(),
                        request.periodEnd()
                );

        log.info(
                "Daily report source data prepared: periodStart={}, periodEnd={}, usersCount={}",
                response.periodStart(),
                response.periodEnd(),
                response.users().size()
        );

        return response;
    }

}
