package com.example.task_manager_backend.config;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public final class SecurityPaths {

    public static final List<String> PUBLIC_PATHS = List.of(
            "/",
            "/index.html",
            "/config.js",
            "/favicon.ico",
            "/assets/**",

            "/login",
            "/api/auth/sign-in",
            "/api/auth/sign-up",
            "/error",

            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );
}