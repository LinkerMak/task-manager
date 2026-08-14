package com.example.task_manager_backend.services.registration;

import com.example.task_manager_backend.dto.web.security.RegisterRequest;

public interface UserRegisterService {

    String register(RegisterRequest request);

}
