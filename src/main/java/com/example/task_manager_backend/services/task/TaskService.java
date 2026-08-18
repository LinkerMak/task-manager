package com.example.task_manager_backend.services.task;

import com.example.task_manager_backend.dto.web.task.*;
import com.example.task_manager_backend.dto.web.task.update.UpdateDescriptionRequest;
import com.example.task_manager_backend.dto.web.task.update.UpdateTaskStatusRequest;
import com.example.task_manager_backend.dto.web.task.update.UpdateTitleRequest;

import java.util.List;

public interface TaskService {

    TaskResponse create(TaskRequest taskRequest, Long userId);

    TaskResponse updateDescription(Long taskId, UpdateDescriptionRequest descriptionRequest, Long userId);

    TaskResponse updateTitle(Long taskId, UpdateTitleRequest titleRequest, Long userId);

    TaskResponse changeStatus(Long taskId, UpdateTaskStatusRequest updateTaskStatusRequest, Long userId);

    void delete(Long taskId, Long userId);

    List<TaskResponse> getAllTasksForUser(Long userId);
}
