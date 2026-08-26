package com.example.emailsender.messaging.model;

import com.example.emailsender.messaging.model.validation.messages.ValidationMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EmailSendingTask(
        @NotNull(message = ValidationMessages.MESSAGE_ID_MUST_NOT_BE_NULL)
        UUID messageId,

        @NotBlank(message = ValidationMessages.RECIPIENT_EMAIL_MUST_NOT_BE_BLANK)
        @Email(message = ValidationMessages.RECIPIENT_EMAIL_MUST_BE_VALID)
        String recipientEmail,

        @NotBlank(message = ValidationMessages.SUBJECT_MUST_NOT_BE_BLANK)
        String subject,

        @NotBlank(message = ValidationMessages.BODY_MUST_NOT_BE_BLANK)
        String body
) {
}
