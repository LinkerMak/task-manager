package com.example.task_manager_backend.dto.event;

public record UserRegisteredEvent(
        Long userId,
        String email
) {
}
