package org.example.scheduler.client;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.scheduler.client.exceptions.TaskSummaryUnavailableException;
import org.example.scheduler.config.SummaryRpcProperties;
import org.example.taskmanager.contracts.summary.TaskSummaryRequest;
import org.example.taskmanager.contracts.summary.TaskSummaryResponse;
import org.example.taskmanager.contracts.summary.TaskSummaryTopics;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class KafkaTaskSummaryClient implements TaskSummaryClient {

    private static final String EXCEPTION_MESSAGE = "Failed to receive task summary for requestId=%s";

    private final ReplyingKafkaTemplate<
            String,
            TaskSummaryRequest,
            TaskSummaryResponse
            > taskSummaryReplyingTemplate;

    private final SummaryRpcProperties properties;

    @Override
    public TaskSummaryResponse summarize(TaskSummaryRequest request) {
        ProducerRecord<String, TaskSummaryRequest> producerRecord
                = new ProducerRecord<>(
                TaskSummaryTopics.TASK_SUMMARY_REQUESTS,
                request.requestId().toString(),
                request
        );

        try {
            return taskSummaryReplyingTemplate
                    .sendAndReceive(producerRecord, properties.replyTimeout())
                    .get(properties.replyTimeout().toMillis(), TimeUnit.MILLISECONDS)
                    .value();
        } catch (Exception e) {
            throw new TaskSummaryUnavailableException(
                    EXCEPTION_MESSAGE.formatted(request.requestId()),
                    e
            );
        }
    }
}
