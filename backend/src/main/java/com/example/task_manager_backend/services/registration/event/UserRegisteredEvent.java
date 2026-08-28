package com.example.task_manager_backend.services.registration.event;

public record UserRegisteredEvent(
        Long userId,
        String email
) {
}
