package com.example.emailsender.messaging.consumer;

import com.example.emailsender.services.EmailDeliveryProcessingService;
import org.example.taskmanager.contracts.email.EmailSendingTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailSendingTaskListenerTest {

    @Mock
    private EmailDeliveryProcessingService emailDeliveryProcessingService;

    @InjectMocks
    private EmailSendingTaskListener emailSendingTaskListener;

    @Test
    void handleDelegatesEmailSendingTaskToProcessingService() {
        EmailSendingTask task = new EmailSendingTask(
                UUID.randomUUID(),
                "user@example.com",
                "Welcome to TaskManager",
                "Welcome to Task Manager!"
        );

        emailSendingTaskListener.handle(task);

        ArgumentCaptor<EmailSendingTask> taskCaptor =
                ArgumentCaptor.forClass(EmailSendingTask.class);

        verify(emailDeliveryProcessingService)
                .process(taskCaptor.capture());

        EmailSendingTask processedTask =
                taskCaptor.getValue();

        assertThat(processedTask).isEqualTo(task);
    }
}