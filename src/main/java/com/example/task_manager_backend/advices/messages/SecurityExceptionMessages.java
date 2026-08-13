package com.example.task_manager_backend.advices.messages;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class SecurityExceptionMessages {
    public static final String USER_ALREADY_EXISTS_MESSAGE = "User already exists";
    public static final String INVALID_EMAIL_OR_PASSWORD_MESSAGE = "Invalid email or password";
    public static final String VALIDATION_EXCEPTION_MESSAGE = "Validation exception";
}
