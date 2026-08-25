package com.example.emailsender.messaging.model;

import java.util.UUID;

public record EmailSendingTask(
        UUID messageId,
        String recipientEmail,
        String subject,
        String body
) {
}
