package com.example.task_manager_backend.services.user;

import com.example.task_manager_backend.dto.web.user.CurrentUserResponse;

public interface CurrentUserService {

    CurrentUserResponse getUser(Long id);
}
