package com.example.emailsender.services;

import com.example.emailsender.messaging.model.EmailSendingTask;

public interface EmailDeliveryProcessingService {
    void process(EmailSendingTask emailSendingTask);
}
