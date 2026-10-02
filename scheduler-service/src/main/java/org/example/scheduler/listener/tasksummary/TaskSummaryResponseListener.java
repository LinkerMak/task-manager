package org.example.scheduler.listener.tasksummary;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scheduler.service.dailyreport.summary.response.DailyReportSummaryResponseService;
import org.example.taskmanager.contracts.summary.TaskSummaryResponse;
import org.example.taskmanager.contracts.summary.topics.TaskSummaryTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskSummaryResponseListener {

    private static final String GROUP_ID =
            "scheduler.task-summary-responses";

    private final DailyReportSummaryResponseService dailyReportSummaryResponseService;

    @KafkaListener(
            topics = TaskSummaryTopics.TASK_SUMMARY_RESPONSES,
            groupId = GROUP_ID,
            containerFactory = "taskSummaryResponseKafkaListenerContainerFactory"
    )
    public void handle(
            @Valid @Payload TaskSummaryResponse response
    ) {
        log.info(
                "Received task summary response: requestId={}",
                response.requestId()
        );

        dailyReportSummaryResponseService.handle(response);
    }
}