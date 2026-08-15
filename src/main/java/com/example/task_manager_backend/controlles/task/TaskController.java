package com.example.task_manager_backend.controlles.task;

import com.example.task_manager_backend.dto.web.task.TaskRequest;
import com.example.task_manager_backend.dto.web.task.TaskResponse;
import com.example.task_manager_backend.services.task.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
