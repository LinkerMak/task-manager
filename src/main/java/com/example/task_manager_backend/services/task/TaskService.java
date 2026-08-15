package com.example.task_manager_backend.services.task;

import com.example.task_manager_backend.dto.web.task.TaskRequest;
import com.example.task_manager_backend.dto.web.task.TaskResponse;

public interface TaskService {

    TaskResponse create(TaskRequest taskRequest, Long userId);
}
