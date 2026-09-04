package org.example.summarizationservice.messaging.kafka.listener;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.summarizationservice.service.GenerateTaskSummaryService;
import org.example.taskmanager.contracts.summary.TaskSummaryRequest;
import org.example.taskmanager.contracts.summary.TaskSummaryResponse;
import org.example.taskmanager.contracts.summary.TaskSummaryTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
public class TaskSummaryKafkaListener {

    private final GenerateTaskSummaryService generateTaskSummaryService;

    @KafkaListener(
            topics = TaskSummaryTopics.TASK_SUMMARY_REQUESTS,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @SendTo
    public TaskSummaryResponse handle(@Payload @Valid TaskSummaryRequest request) {
        log.info(
                "Received task summary request: requestId={}, tasksCount={}",
                request.requestId(),
                request.tasks().size()
        );

        TaskSummaryResponse response =
                generateTaskSummaryService.generate(request);

        log.info(
                "Published task summary response: requestId={}",
                response.requestId()
        );

        return response;
    }
}
