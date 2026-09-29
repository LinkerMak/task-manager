package com.example.task_manager_backend.security.config;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public final class SecurityPaths {

    public static final List<String> PUBLIC_PATHS = List.of(
            "/users",

            "/auth/login",
            "/auth/logout"
    );

    public static final List<String> ACTUATOR_HEALTH_PATHS = List.of(
            "/actuator/health",
            "/actuator/health/**"
    );

    public static final List<String> SWAGGER_PATHS = List.of(
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );

    public final String ERROR_PATH = "/error";
}