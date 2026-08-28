package com.example.emailsender.services;


import org.example.taskmanager.contracts.email.EmailSendingTask;

public interface EmailDeliveryProcessingService {
    void process(EmailSendingTask emailSendingTask);
}
