package com.example.task_manager_backend.advices;

import com.example.task_manager_backend.controlles.AuthController;
import com.example.task_manager_backend.controlles.UserController;
import com.example.task_manager_backend.dto.web.exception.ExceptionResponse;
import com.example.task_manager_backend.exceptions.authentication.InvalidCredentialsException;
import com.example.task_manager_backend.exceptions.registration.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.task_manager_backend.advices.messages.SecurityExceptionMessages.*;

@Slf4j
@RestControllerAdvice(basePackageClasses = {
        AuthController.class,
        UserController.class}
)
public class SecurityControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();

        String message = fieldError != null
                ? fieldError.getDefaultMessage()
                : VALIDATION_EXCEPTION_MESSAGE;

        log.warn(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(message));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        log.warn(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ExceptionResponse(USER_ALREADY_EXISTS_MESSAGE));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(InvalidCredentialsException e) {
        log.warn(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponse(INVALID_EMAIL_OR_PASSWORD_MESSAGE));
    }
}
