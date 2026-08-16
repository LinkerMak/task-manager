package com.example.task_manager_backend.services.task;

import com.example.task_manager_backend.dto.web.task.TaskRequest;
import com.example.task_manager_backend.dto.web.task.TaskResponse;

import java.util.List;

public interface TaskService {

    TaskResponse create(TaskRequest taskRequest, Long userId);

    TaskResponse updateTask(Long taskId, TaskRequest taskRequest, Long userId);

    TaskResponse completeTask(Long taskId, Long userId);

    TaskResponse reopenTask(Long taskId, Long userId);

    void delete(Long taskId, Long userId);

    List<TaskResponse> getAllTasksForUser(Long userId);
}
