package org.example.scheduler.service.dailyreport.creation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scheduler.entity.dailyreport.DailyReportWorkflow;
import org.example.scheduler.entity.outboxevent.OutboxEvent;
import org.example.scheduler.repository.dailyreport.DailyReportWorkflowRepository;
import org.example.scheduler.repository.outbox.OutboxEventRepository;
import org.example.taskmanager.contracts.dailyreport.DailyReportUserTasksReady;
import org.example.taskmanager.contracts.summary.TaskSummaryRequest;
import org.example.taskmanager.contracts.summary.topics.TaskSummaryTopics;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyReportWorkflowCreationServiceImpl implements DailyReportWorkflowCreationService {

    private final DailyReportWorkflowRepository workflowRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public void createIfAbsent(
            DailyReportUserTasksReady message
    ) {
        boolean workflowExists =
                workflowRepository
                        .findByUserIdAndPeriodStartAndPeriodEnd(
                                message.userId(),
                                message.periodStart(),
                                message.periodEnd()
                        )
                        .isPresent();

        if (workflowExists) {
            log.info(
                    "Daily report workflow already exists, skipping duplicate message: userId={}, periodStart={}, periodEnd={}",
                    message.userId(),
                    message.periodStart(),
                    message.periodEnd()
            );
            return;
        }

        UUID requestId = UUID.randomUUID();

        DailyReportWorkflow workflow =
                DailyReportWorkflow.requested(
                        requestId,
                        message.userId(),
                        message.email(),
                        message.periodStart(),
                        message.periodEnd()
                );

        TaskSummaryRequest summaryRequest =
                new TaskSummaryRequest(
                        requestId,
                        message.periodStart(),
                        message.periodEnd(),
                        message.tasks()
                );

        String payload = serialize(summaryRequest);

        OutboxEvent outboxEvent =
                OutboxEvent.pending(
                        requestId,
                        TaskSummaryTopics.TASK_SUMMARY_REQUESTS,
                        requestId.toString(),
                        payload
                );

        workflowRepository.save(workflow);
        outboxEventRepository.save(outboxEvent);

        log.info(
                "Daily report workflow and summary outbox event created: requestId={}, userId={}, recipientEmail={}, tasksCount={}, periodStart={}, periodEnd={}",
                requestId,
                message.userId(),
                message.email(),
                message.tasks().size(),
                message.periodStart(),
                message.periodEnd()
        );
    }

    private String serialize(
            TaskSummaryRequest summaryRequest
    ) {
        try {
            return objectMapper.writeValueAsString(summaryRequest);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Failed to serialize task summary request",
                    exception
            );
        }
    }
}