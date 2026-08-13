package com.example.task_manager_backend.services;

import com.example.task_manager_backend.dto.web.security.LoginRequest;

public interface UserAuthService {

    String login(LoginRequest request);
}
