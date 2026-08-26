package com.example.emailsender.messaging.model.validation.messages;

public final class ValidationMessages {

    public static final String MESSAGE_ID_MUST_NOT_BE_NULL =
            "Email sending task messageId must not be null";

    public static final String RECIPIENT_EMAIL_MUST_NOT_BE_BLANK =
            "Recipient email must not be blank";

    public static final String RECIPIENT_EMAIL_MUST_BE_VALID =
            "Recipient email must be valid";

    public static final String SUBJECT_MUST_NOT_BE_BLANK =
            "Email subject must not be blank";

    public static final String BODY_MUST_NOT_BE_BLANK =
            "Email body must not be blank";

    private ValidationMessages() {
    }
}