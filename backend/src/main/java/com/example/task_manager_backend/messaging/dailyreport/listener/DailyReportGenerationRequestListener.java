package com.example.task_manager_backend.messaging.dailyreport.listener;

import com.example.task_manager_backend.messaging.dailyreport.producer.DailyReportUserTasksReadyProducer;
import com.example.task_manager_backend.services.dailyreport.DailyReportSourceDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanager.contracts.dailyreport.DailyReportGenerationRequest;
import org.example.taskmanager.contracts.dailyreport.DailyReportUserTasksReady;
import org.example.taskmanager.contracts.dailyreport.topics.DailyReportTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyReportGenerationRequestListener {

    private static final String GROUP_ID =
            "task-manager-backend.daily-report-generation";

    private final DailyReportSourceDataService dailyReportSourceDataService;
    private final DailyReportUserTasksReadyProducer dailyReportUserTasksReadyProducer;

    @KafkaListener(
            topics = DailyReportTopics.GENERATION_REQUESTS,
            groupId = GROUP_ID,
            containerFactory = "dailyReportKafkaListenerContainerFactory"
    )
    public void handle(
            @Valid @Payload DailyReportGenerationRequest request
    ) {
        log.info(
                "Received daily report generation request: periodStart={}, periodEnd={}",
                request.periodStart(),
                request.periodEnd()
        );

        List<DailyReportUserTasksReady> users =
                dailyReportSourceDataService.getUsersWithTasks(
                        request.periodStart(),
                        request.periodEnd()
                );

        log.info(
                "Daily report source data prepared: periodStart={}, periodEnd={}, usersCount={}",
                request.periodStart(),
                request.periodEnd(),
                users.size()
        );

        for (DailyReportUserTasksReady user : users) {
            dailyReportUserTasksReadyProducer.publish(user);
        }

        log.info(
                "Daily report user task messages submitted for publishing: periodStart={}, periodEnd={}, usersCount={}",
                request.periodStart(),
                request.periodEnd(),
                users.size()
        );
    }
}