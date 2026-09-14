package org.example.scheduler.service.dailyreport.summary.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scheduler.email.composer.DailyReportEmailComposer;
import org.example.scheduler.entity.dailyreport.DailyReportWorkflow;
import org.example.scheduler.entity.dailyreport.DailyReportWorkflowStatus;
import org.example.scheduler.entity.outboxevent.OutboxEvent;
import org.example.scheduler.repository.dailyreport.DailyReportWorkflowRepository;
import org.example.scheduler.repository.outbox.OutboxEventRepository;
import org.example.taskmanager.contracts.email.EmailSendingTask;
import org.example.taskmanager.contracts.email.topics.EmailSendingTopics;
import org.example.taskmanager.contracts.summary.TaskSummaryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class DailyReportSummaryResponseService {

    private final DailyReportWorkflowRepository workflowRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final DailyReportEmailComposer dailyReportEmailComposer;
    private final ObjectMapper objectMapper;

    @Transactional
    public void handle(TaskSummaryResponse response) {
        DailyReportWorkflow workflow =
                workflowRepository.findById(response.requestId())
                        .orElseThrow(() -> new IllegalStateException(
                                "Daily report workflow not found for requestId="
                                        + response.requestId()
                        ));

        if (workflow.getStatus() == DailyReportWorkflowStatus.EMAIL_QUEUED) {
            log.info(
                    "Skipping duplicate task summary response: requestId={}",
                    response.requestId()
            );
            return;
        }

        workflow.markSummaryGenerated(response.summaryText());

        EmailSendingTask emailSendingTask =
                dailyReportEmailComposer.compose(
                        workflow.getUserId(),
                        workflow.getRecipientEmail(),
                        workflow.getPeriodStart(),
                        workflow.getPeriodEnd(),
                        workflow.getSummaryText()
                );

        OutboxEvent emailOutboxEvent =
                OutboxEvent.pending(
                        workflow.getRequestId(),
                        EmailSendingTopics.EMAIL_SENDING_TASKS,
                        emailSendingTask.messageId().toString(),
                        serialize(emailSendingTask)
                );

        outboxEventRepository.save(emailOutboxEvent);

        workflow.markEmailQueued();

        log.info(
                "Daily report summary processed and email outbox event created: requestId={}, userId={}, recipientEmail={}, emailMessageId={}",
                workflow.getRequestId(),
                workflow.getUserId(),
                workflow.getRecipientEmail(),
                emailSendingTask.messageId()
        );
    }

    private String serialize(EmailSendingTask emailSendingTask) {
        try {
            return objectMapper.writeValueAsString(emailSendingTask);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Failed to serialize email sending task",
                    exception
            );
        }
    }
}