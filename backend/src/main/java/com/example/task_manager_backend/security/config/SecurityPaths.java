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
}