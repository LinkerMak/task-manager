package com.example.task_manager_backend.dto.web.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.example.task_manager_backend.dto.web.security.messages.ValidationMessages.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest implements EmailPasswordRequest {

    public static final String PASSWORD_PATTERN = "^[a-zA-Z0-9]+[a-zA-Z_0-9]*[a-zA-Z0-9]+$";

    @NotBlank(message = EMAIL_REQUIRED)
    @Email(message = EMAIL_INVALID_FORMAT)
    @Size(max = 255, message = EMAIL_SIZE)
    private String email;

    @NotBlank(message = PASSWORD_REQUIRED)
    @Size(min = 5, max = 20, message = PASSWORD_SIZE)
    @Pattern(regexp = PASSWORD_PATTERN, message = PASSWORD_INVALID_FORMAT)
    private String password;
}
