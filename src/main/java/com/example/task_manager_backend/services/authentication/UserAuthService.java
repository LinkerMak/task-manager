package com.example.task_manager_backend.services.authentication;

import com.example.task_manager_backend.dto.web.security.LoginRequest;

public interface UserAuthService {

    String login(LoginRequest request);
}
