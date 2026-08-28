package com.example.task_manager_backend.messaging.producer.listener;

import com.example.task_manager_backend.messaging.producer.EmailSendingTaskProducer;
import com.example.task_manager_backend.services.registration.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanager.contracts.email.EmailSendingTask;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WelcomeEmailOnUserRegisteredListener {

    private static final String WELCOME_EMAIL_SUBJECT =
            "Welcome to TaskManager";

    private static final String WELCOME_EMAIL_BODY = """
            Welcome to Task Manager!
            
            Your account has been successfully created.
            Now you can create tasks and mark them as completed.
            """;

    private final EmailSendingTaskProducer emailSendingTaskProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserRegisteredEvent event) {

        EmailSendingTask task = new EmailSendingTask(
                UUID.randomUUID(),
                event.email(),
                WELCOME_EMAIL_SUBJECT,
                WELCOME_EMAIL_BODY
        );

        emailSendingTaskProducer.send(task);

        log.info(
                "Welcome email task created: userId={}, messageId={}, recipientEmail={}",
                event.userId(),
                task.messageId(),
                task.recipientEmail()
        );
    }
}
