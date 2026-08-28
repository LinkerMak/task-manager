package com.example.task_manager_backend.controllers.task;

import com.example.task_manager_backend.dto.web.pages.PagedResponse;
import com.example.task_manager_backend.dto.web.task.TaskRequest;
import com.example.task_manager_backend.dto.web.task.TaskResponse;
import com.example.task_manager_backend.dto.web.task.update.UpdateDescriptionRequest;
import com.example.task_manager_backend.dto.web.task.update.UpdateTaskStatusRequest;
import com.example.task_manager_backend.dto.web.task.update.UpdateTitleRequest;
import com.example.task_manager_backend.services.task.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest,
                                                   @AuthenticationPrincipal Long userId) {

        TaskResponse taskResponse = taskService.create(taskRequest, userId);

        URI locationForCreated = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(taskResponse.id())
                .toUri();

        return ResponseEntity
                .created(locationForCreated)
                .body(taskResponse);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId,
                                           @AuthenticationPrincipal Long userId) {
        taskService.delete(taskId, userId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PatchMapping("/{taskId}/description")
    public ResponseEntity<TaskResponse> updateDescription(@Valid @RequestBody UpdateDescriptionRequest descriptionRequest,
                                                          @PathVariable Long taskId,
                                                          @AuthenticationPrincipal Long userId) {
        TaskResponse taskResponse = taskService.updateDescription(taskId, descriptionRequest, userId);
        return ResponseEntity
                .ok()
                .body(taskResponse);
    }

    @PatchMapping("/{taskId}/title")
    public ResponseEntity<TaskResponse> updateTitle(@Valid @RequestBody UpdateTitleRequest titleRequest,
                                                    @PathVariable Long taskId,
                                                    @AuthenticationPrincipal Long userId) {
        TaskResponse taskResponse = taskService.updateTitle(taskId, titleRequest, userId);
        return ResponseEntity
                .ok()
                .body(taskResponse);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> changeStatus(@Valid @RequestBody UpdateTaskStatusRequest updateTaskStatusRequest,
                                                     @PathVariable Long taskId,
                                                     @AuthenticationPrincipal Long userId) {
        TaskResponse taskResponse = taskService.changeStatus(taskId, updateTaskStatusRequest, userId);
        return ResponseEntity
                .ok()
                .body(taskResponse);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TaskResponse>> getAllTasks(@AuthenticationPrincipal Long userId,
                                                                   @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                                   Pageable pageable) {
        PagedResponse<TaskResponse> taskResponses = taskService.getAllTasksForUser(userId, pageable);
        return ResponseEntity
                .ok()
                .body(taskResponses);
    }
}
