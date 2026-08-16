package com.example.task_manager_backend.advices.messages;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ExceptionMessages {

    public static final String RESOURCE_NOT_FOUND_MESSAGE = "Resource not found";
    public static final String RESOURCE_ALREADY_EXISTS_MESSAGE = "Resource already exists";

    public static final String INVALID_EMAIL_OR_PASSWORD_MESSAGE = "Invalid email or password";
    public static final String VALIDATION_EXCEPTION_MESSAGE = "Validation exception";

    public static final String DATA_INTEGRITY_VIOLATION = "Operation cannot be completed because related data was changed or deleted.";

    public static final String UNKNOWN_ERROR = "Unknown error";


}
