package com.example.emailsender.services;

import com.example.emailsender.messaging.model.EmailSendingTask;

public interface EmailSendingService {

    void send(EmailSendingTask emailSendingTask);

}
