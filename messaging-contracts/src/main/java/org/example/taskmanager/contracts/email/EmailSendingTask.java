package org.example.taskmanager.contracts.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EmailSendingTask(
        @NotNull
        UUID messageId,

        @NotBlank
        @Email
        String recipientEmail,

        @NotBlank
        String subject,

        @NotBlank
        String body
) {
}
