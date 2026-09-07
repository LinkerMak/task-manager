package org.example.scheduler.client.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanager.contracts.email.EmailSendingTask;
import org.example.taskmanager.contracts.email.topics.EmailSendingTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEmailSendingTaskProducer implements EmailSendingTaskClient{

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final KafkaTemplate<String, EmailSendingTask> kafkaTemplate;

    @Override
    public void send(EmailSendingTask task) {
        kafkaTemplate.send(
                EmailSendingTopics.EMAIL_SENDING_TASKS,
                task.messageId().toString(),
                task
        ).whenComplete((result, exception) -> {
            if(exception == null) {
                log.info(
                        "Daily report email task published: messageId={}, recipientEmail={}, topic={}, partition={}, offset={}",
                        task.messageId(),
                        task.recipientEmail(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
                return;
            }

            log.error(
                    "Failed to publish daily report email task: messageId={}, recipientEmail={}",
                    task.messageId(),
                    task.recipientEmail(),
                    exception
            );
        });
    }
}
