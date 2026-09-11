package org.example.scheduler.service.dailyreport.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scheduler.client.email.EmailSendingTaskClient;
import org.example.scheduler.client.tasksummary.TaskSummaryClient;
import org.example.scheduler.service.dailyreport.processor.email.composer.DailyReportEmailComposer;
import org.example.taskmanager.contracts.dailyreport.DailyReportUserData;
import org.example.taskmanager.contracts.email.EmailSendingTask;
import org.example.taskmanager.contracts.summary.TaskSummaryRequest;
import org.example.taskmanager.contracts.summary.TaskSummaryResponse;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyReportUserProcessor {

    private final TaskSummaryClient taskSummaryClient;
    private final DailyReportEmailComposer dailyReportEmailComposer;
    private final EmailSendingTaskClient emailSendingTaskClient;

    public void process(
            DailyReportUserData user,
            OffsetDateTime periodStart,
            OffsetDateTime periodEnd
    ) {
        log.info(
                "Processing daily report for user: userId={}, recipientEmail={}, tasksCount={}, periodStart={}, periodEnd={}",
                user.userId(),
                user.email(),
                user.tasks().size(),
                periodStart,
                periodEnd
        );

        TaskSummaryRequest summaryRequest =
                createTaskSummaryRequest(user, periodStart, periodEnd);

        TaskSummaryResponse summaryResponse =
                taskSummaryClient.summarize(summaryRequest);

        validateSummaryResponse(summaryRequest, summaryResponse);

        EmailSendingTask emailSendingTask =
                dailyReportEmailComposer.compose(
                        user,
                        periodStart,
                        periodEnd,
                        summaryResponse.summaryText()
                );

        emailSendingTaskClient.send(emailSendingTask);

        log.info(
                "Daily report email task created: userId={}, recipientEmail={}, messageId={}, summaryRequestId={}",
                user.userId(),
                user.email(),
                emailSendingTask.messageId(),
                summaryRequest.requestId()
        );
    }

    private TaskSummaryRequest createTaskSummaryRequest(
            DailyReportUserData user,
            OffsetDateTime periodStart,
            OffsetDateTime periodEnd
    ) {
        return new TaskSummaryRequest(
                UUID.randomUUID(),
                periodStart,
                periodEnd,
                user.tasks()
        );
    }

    private void validateSummaryResponse(
            TaskSummaryRequest request,
            TaskSummaryResponse response
    ) {
        if (response == null) {
            throw new IllegalStateException(
                    "Summarization service returned an empty response"
            );
        }

        if (!request.requestId().equals(response.requestId())) {
            throw new IllegalStateException(
                    "Summarization service returned a response for an unexpected request"
            );
        }
    }
}
