package com.example.emailsender.messaging.consumer;

import com.example.emailsender.messaging.config.KafkaTopicConfiguration;
import com.example.emailsender.messaging.model.EmailSendingTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailSendingTaskListener {

    @KafkaListener(
            topics = KafkaTopicConfiguration.EMAIL_SENDING_TASKS_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handle(EmailSendingTask emailSendingTask) {
        log.info(
                "Received email sending task: messageId={}, recipientEmail={}, subject={}",
                emailSendingTask.messageId(),
                emailSendingTask.recipientEmail(),
                emailSendingTask.subject()
        );
    }
}
