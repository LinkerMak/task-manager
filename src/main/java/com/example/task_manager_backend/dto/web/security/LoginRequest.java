package com.example.task_manager_backend.dto.web.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import static com.example.task_manager_backend.dto.web.security.messages.ValidationMessages.*;

@Getter
public class LoginRequest implements EmailPasswordRequest {

    @NotBlank(message = EMAIL_REQUIRED)
    @Size(max = 255, message = EMAIL_SIZE)
    private String email;

    @NotBlank(message = PASSWORD_REQUIRED)
    @Size(min = 5, max = 20, message = PASSWORD_SIZE)
    private String password;
}
