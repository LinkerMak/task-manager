package com.example.task_manager_backend.services.task;

import com.example.task_manager_backend.dto.web.pages.PagedResponse;
import com.example.task_manager_backend.dto.web.task.TaskRequest;
import com.example.task_manager_backend.dto.web.task.TaskResponse;
import com.example.task_manager_backend.dto.web.task.update.UpdateDescriptionRequest;
import com.example.task_manager_backend.dto.web.task.update.UpdateTaskStatusRequest;
import com.example.task_manager_backend.dto.web.task.update.UpdateTitleRequest;
import org.springframework.data.domain.Pageable;


public interface TaskService {

    TaskResponse create(TaskRequest taskRequest, Long userId);

    TaskResponse updateDescription(Long taskId, UpdateDescriptionRequest descriptionRequest, Long userId);

    TaskResponse updateTitle(Long taskId, UpdateTitleRequest titleRequest, Long userId);

    TaskResponse changeStatus(Long taskId, UpdateTaskStatusRequest updateTaskStatusRequest, Long userId);

    void delete(Long taskId, Long userId);

    PagedResponse<TaskResponse> getAllTasksForUser(Long userId, Pageable pageable);
}
