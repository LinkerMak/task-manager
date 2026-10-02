package com.example.task_manager_backend.mappers;

import com.example.task_manager_backend.dto.web.task.TaskResponse;
import com.example.task_manager_backend.models.task.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCompletedAt()
        );
    }
}