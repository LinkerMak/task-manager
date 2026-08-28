package com.example.task_manager_backend.messaging.producer;

import org.example.taskmanager.contracts.email.EmailSendingTask;

public interface EmailSendingTaskProducer {
    void send(EmailSendingTask task);
}
