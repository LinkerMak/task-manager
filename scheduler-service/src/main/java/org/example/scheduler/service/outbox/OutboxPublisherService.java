package org.example.scheduler.service.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scheduler.entity.outboxevent.OutboxEvent;
import org.example.scheduler.entity.outboxevent.OutboxEventStatus;
import org.example.scheduler.repository.outbox.OutboxEventRepository;
import org.example.taskmanager.contracts.email.EmailSendingTask;
import org.example.taskmanager.contracts.email.topics.EmailSendingTopics;
import org.example.taskmanager.contracts.summary.TaskSummaryRequest;
import org.example.taskmanager.contracts.summary.topics.TaskSummaryTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxPublisherService {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final OutboxEventStatusService outboxEventStatusService;

    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents =
                outboxEventRepository.findAllByStatus(
                        OutboxEventStatus.PENDING
                );

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info(
                "Publishing pending outbox events: count={}",
                pendingEvents.size()
        );

        for (OutboxEvent event : pendingEvents) {
            publish(event);
        }
    }

    private void publish(OutboxEvent event) {
        try {
            Object payload = deserializePayload(event);

            kafkaTemplate.send(
                    event.getTopic(),
                    event.getMessageKey(),
                    payload
            ).get();

            outboxEventStatusService.markPublished(event.getId());

            log.info(
                    "Outbox event published: outboxEventId={}, workflowRequestId={}, topic={}, messageKey={}",
                    event.getId(),
                    event.getWorkflowRequestId(),
                    event.getTopic(),
                    event.getMessageKey()
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            log.error(
                    "Outbox event publishing interrupted: outboxEventId={}, workflowRequestId={}, topic={}",
                    event.getId(),
                    event.getWorkflowRequestId(),
                    event.getTopic(),
                    exception
            );
        } catch (ExecutionException | JsonProcessingException exception) {
            log.error(
                    "Failed to publish outbox event: outboxEventId={}, workflowRequestId={}, topic={}",
                    event.getId(),
                    event.getWorkflowRequestId(),
                    event.getTopic(),
                    exception
            );
        }
    }

    private Object deserializePayload(OutboxEvent event)
            throws JsonProcessingException {

        if (TaskSummaryTopics.TASK_SUMMARY_REQUESTS.equals(event.getTopic())) {
            return objectMapper.readValue(
                    event.getPayload(),
                    TaskSummaryRequest.class
            );
        }

        if (EmailSendingTopics.EMAIL_SENDING_TASKS.equals(event.getTopic())) {
            return objectMapper.readValue(
                    event.getPayload(),
                    EmailSendingTask.class
            );
        }

        throw new IllegalArgumentException(
                "Unsupported outbox topic: " + event.getTopic()
        );
    }
}