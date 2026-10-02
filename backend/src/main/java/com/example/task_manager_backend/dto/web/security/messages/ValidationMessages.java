package com.example.task_manager_backend.dto.web.security.messages;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationMessages {
    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String PASSWORD_SIZE = "Password must be between 5 and 20 characters";
    public static final String PASSWORD_INVALID_FORMAT = "Password format is invalid";

    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String EMAIL_INVALID_FORMAT = "Email format is invalid";
    public static final String EMAIL_SIZE = "Email must not exceed 100 characters";
}
