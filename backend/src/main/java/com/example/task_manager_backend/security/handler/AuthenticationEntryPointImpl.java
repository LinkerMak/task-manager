package com.example.task_manager_backend.security.handler;

import com.example.task_manager_backend.dto.web.exception.ExceptionResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    private static final String UNAUTHORIZED_MESSAGE =
            "Authentication is required";

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.warn("Unauthorized request: method = {}, uri = {}, reason = {}, message = {}",
                request.getMethod(),
                request.getRequestURI(),
                authException.getClass().getSimpleName(),
                authException.getMessage());

        ExceptionResponse exceptionResponse = new ExceptionResponse(
                UNAUTHORIZED_MESSAGE
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(
                response.getOutputStream(),
                exceptionResponse
        );
    }
}
