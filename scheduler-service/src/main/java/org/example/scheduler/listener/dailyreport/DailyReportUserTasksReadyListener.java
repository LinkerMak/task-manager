package org.example.scheduler.listener.dailyreport;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scheduler.service.dailyreport.workflow.DailyReportWorkflowCreationService;
import org.example.taskmanager.contracts.dailyreport.DailyReportUserTasksReady;
import org.example.taskmanager.contracts.dailyreport.topics.DailyReportTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyReportUserTasksReadyListener {

    private static final String GROUP_ID =
            "scheduler.daily-report-user-tasks-ready";

    private final DailyReportWorkflowCreationService workflowCreationService;

    @KafkaListener(
            topics = DailyReportTopics.USER_TASKS_READY,
            groupId = GROUP_ID,
            containerFactory = "dailyReportUserTasksReadyKafkaListenerContainerFactory"
    )
    public void handle(
            @Valid @Payload DailyReportUserTasksReady message
    ) {
        log.info(
                "Received daily report user tasks: userId={}, recipientEmail={}, tasksCount={}, periodStart={}, periodEnd={}",
                message.userId(),
                message.email(),
                message.tasks().size(),
                message.periodStart(),
                message.periodEnd()
        );

        workflowCreationService.createIfAbsent(message);
    }
}