package com.example.task_manager_backend.controlles.task;

import com.example.task_manager_backend.dto.web.task.TaskRequest;
import com.example.task_manager_backend.dto.web.task.TaskResponse;
import com.example.task_manager_backend.services.task.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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

    @PutMapping("/taskId")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long taskId,
                                                   @Valid @RequestBody TaskRequest taskRequest,
                                                   @AuthenticationPrincipal Long userId) {
        TaskResponse taskResponse = taskService.updateTask(taskId, taskRequest, userId);
        return ResponseEntity
                .ok()
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

    @PatchMapping("/{taskId}/completed")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable Long taskId,
                                                     @AuthenticationPrincipal Long userId) {
        TaskResponse taskResponse = taskService.completeTask(taskId, userId);
        return ResponseEntity
                .ok()
                .body(taskResponse);
    }

    @PatchMapping("/{taskId}/reopen")
    public ResponseEntity<TaskResponse> reopenTask(@PathVariable Long taskId,
                                                   @AuthenticationPrincipal Long userId) {
        TaskResponse taskResponse = taskService.reopenTask(taskId, userId);
        return ResponseEntity
                .ok()
                .body(taskResponse);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(@AuthenticationPrincipal Long userId) {
        List<TaskResponse> taskResponses = taskService.getAllTasksForUser(userId);
        return ResponseEntity
                .ok()
                .body(taskResponses);
    }
}
