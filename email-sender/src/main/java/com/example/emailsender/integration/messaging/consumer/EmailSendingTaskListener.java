package com.example.emailsender.integration.messaging.consumer;

import com.example.emailsender.services.EmailDeliveryProcessingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanager.contracts.email.EmailSendingTask;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSendingTaskListener {

    private final EmailDeliveryProcessingService emailDeliveryProcessingService;

    @KafkaListener(
            topics = "${spring.kafka.topics.email-sending-tasks}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handle(@Payload @Valid EmailSendingTask emailSendingTask) {
        log.info(
                "Received email sending task: messageId={}, recipientEmail={}, subject={}",
                emailSendingTask.messageId(),
                emailSendingTask.recipientEmail(),
                emailSendingTask.subject()
        );

        emailDeliveryProcessingService.process(emailSendingTask);
    }
}
