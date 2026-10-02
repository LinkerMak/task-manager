package com.example.task_manager_backend.services.jwt;

public interface JwtService {
    String generateAccessToken(Long userId, String email);

    Long extractUserId(String accessToken);

    boolean isAccessTokenValid(String accessToken);
}
