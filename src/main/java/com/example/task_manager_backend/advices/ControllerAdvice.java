package com.example.task_manager_backend.advices;

import com.example.task_manager_backend.controlles.auth.AuthController;
import com.example.task_manager_backend.controlles.user.UserController;
import com.example.task_manager_backend.dto.web.exception.ExceptionResponse;
import com.example.task_manager_backend.exceptions.authentication.InvalidCredentialsException;
import com.example.task_manager_backend.exceptions.resource.ResourceAlreadyExistsException;
import com.example.task_manager_backend.exceptions.resource.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.task_manager_backend.advices.messages.ExceptionMessages.*;

@Slf4j
@RestControllerAdvice(basePackageClasses = {
        AuthController.class,
        UserController.class}
)
public class ControllerAdvice {

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

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(InvalidCredentialsException e) {
        log.warn(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponse(INVALID_EMAIL_OR_PASSWORD_MESSAGE));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> resourceNotFoundHandler(ResourceNotFoundException e) {
        log.warn(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(RESOURCE_NOT_FOUND_MESSAGE));
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        log.warn(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ExceptionResponse(RESOURCE_ALREADY_EXISTS_MESSAGE));
    }


}
