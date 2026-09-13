package org.example.scheduler.producer.email;

import org.example.taskmanager.contracts.email.EmailSendingTask;

public interface EmailSendingTaskProducer {
    void publish(EmailSendingTask task);
}
