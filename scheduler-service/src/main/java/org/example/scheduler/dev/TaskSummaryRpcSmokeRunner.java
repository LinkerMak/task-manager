package org.example.scheduler.dev;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scheduler.client.TaskSummaryClient;
import org.example.taskmanager.contracts.summary.TaskSummaryRequest;
import org.example.taskmanager.contracts.summary.TaskSummaryTask;
import org.example.taskmanager.contracts.summary.TaskSummaryTaskStatus;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskSummaryRpcSmokeRunner implements ApplicationRunner {

    private final TaskSummaryClient taskSummaryClient;

    @Override
    public void run(ApplicationArguments args) {
        OffsetDateTime now = OffsetDateTime.now();

        TaskSummaryRequest request = new TaskSummaryRequest(
                UUID.randomUUID(),
                now.minusDays(1),
                now,
                List.of(
                        new TaskSummaryTask(
                                1L,
                                "Подготовить Kafka RPC",
                                "Проверить request/reply между Scheduler и Summarization.",
                                TaskSummaryTaskStatus.DONE,
                                now.minusHours(2)
                        ),
                        new TaskSummaryTask(
                                2L,
                                "Реализовать ежедневный отчёт",
                                "После smoke test заменить runner настоящим scheduled job.",
                                TaskSummaryTaskStatus.TODO,
                                null
                        )
                )
        );

        log.info(
                "Sending task summary request: requestId={}, tasksCount={}",
                request.requestId(),
                request.tasks().size()
        );

        var response = taskSummaryClient.summarize(request);

        log.info(
                "Received task summary response: requestId={}, summary={}",
                response.requestId(),
                response.summaryText()
        );
    }
}
