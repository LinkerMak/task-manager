package com.example.emailsender.services;

import org.example.taskmanager.contracts.email.EmailSendingTask;

public interface EmailSendingService {

    void send(EmailSendingTask emailSendingTask);

}
