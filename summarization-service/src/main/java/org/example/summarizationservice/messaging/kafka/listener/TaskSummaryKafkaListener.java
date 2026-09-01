package org.example.summarizationservice.messaging.kafka.listener;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.summarizationservice.usecase.GenerateTaskSummaryUseCase;
import org.example.taskmanager.contracts.summary.TaskSummaryRequest;
import org.example.taskmanager.contracts.summary.TaskSummaryResponse;
import org.example.taskmanager.contracts.summary.TaskSummaryTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
public class TaskSummaryKafkaListener {

    private final GenerateTaskSummaryUseCase generateTaskSummaryUseCase;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(
            topics = TaskSummaryTopics.TASK_SUMMARY_REQUESTS,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handle(@Payload @Valid TaskSummaryRequest request) {
        log.info(
                "Received task summary request: requestId={}, tasksCount={}",
                request.requestId(),
                request.tasks().size()
        );

        TaskSummaryResponse response =
                generateTaskSummaryUseCase.generate(request);

        kafkaTemplate.send(
                TaskSummaryTopics.TASK_SUMMARY_RESPONSES,
                request.requestId().toString(),
                response
        ).join();

        log.info(
                "Published task summary response: requestId={}",
                response.requestId()
        );
    }
}
