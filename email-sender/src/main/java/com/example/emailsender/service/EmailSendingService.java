package com.example.emailsender.service;

import com.example.emailsender.messaging.model.EmailSendingTask;

public interface EmailSendingService {

    void send(EmailSendingTask emailSendingTask);

}
