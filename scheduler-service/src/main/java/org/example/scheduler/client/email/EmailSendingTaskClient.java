package org.example.scheduler.client.email;

import org.example.taskmanager.contracts.email.EmailSendingTask;

public interface EmailSendingTaskClient {
    void send (EmailSendingTask task);
}
