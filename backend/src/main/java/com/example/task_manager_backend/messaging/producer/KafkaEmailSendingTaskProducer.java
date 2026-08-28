package com.example.task_manager_backend.messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanager.contracts.email.EmailSendingTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEmailSendingTaskProducer implements EmailSendingTaskProducer {

    private final KafkaTemplate<String, EmailSendingTask> kafkaTemplate;

    @Value("${spring.app.kafka.topics.email-sending-tasks}")
    private String emailSendingTasksTopic;

    @Override
    public void send(EmailSendingTask task) {
        kafkaTemplate.send(
                emailSendingTasksTopic,
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
