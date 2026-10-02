package com.example.task_manager_backend.messaging.email.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanager.contracts.email.EmailSendingTask;
import org.example.taskmanager.contracts.email.topics.EmailSendingTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEmailSendingTaskProducer implements EmailSendingTaskProducer {

    private final KafkaTemplate<String, EmailSendingTask> kafkaTemplate;

    @Override
    public void send(EmailSendingTask task) {
        kafkaTemplate.send(
                EmailSendingTopics.EMAIL_SENDING_TASKS,
                task
        ).whenComplete(
                (result, exception) -> {
                    if (exception == null) {
                        log.info(
                                "Email sending task published: messageId={}, topic={}, partition={}, offset={}",
                                task.messageId(),
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                        return;
                    }

                    log.error(
                            "Failed to publish email sending task: messageId={}, recipientEmail={}",
                            task.messageId(),
                            task.recipientEmail(),
                            exception
                    );
                }
        );

    }
}
