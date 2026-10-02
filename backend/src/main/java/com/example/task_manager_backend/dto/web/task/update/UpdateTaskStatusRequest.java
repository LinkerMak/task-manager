package com.example.task_manager_backend.dto.web.task.update;

import com.example.task_manager_backend.models.task.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(

        @Schema(
                description = "New task status. Allowed values: TODO, DONE"
        )
        @NotNull(message = "Status must not be null")
        TaskStatus status
) {
}
