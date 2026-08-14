package com.example.task_manager_backend.dto.web.user;

public record CurrentUserResponse(
        Long id,
        String email
) {
}
