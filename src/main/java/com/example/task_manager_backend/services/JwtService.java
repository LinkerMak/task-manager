package com.example.task_manager_backend.services;

public interface JwtService {
    String generateAccessToken(Long userId, String email);
}
