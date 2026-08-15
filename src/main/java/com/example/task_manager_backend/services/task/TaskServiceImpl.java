package com.example.task_manager_backend.services.task;

import com.example.task_manager_backend.dto.web.task.TaskRequest;
import com.example.task_manager_backend.dto.web.task.TaskResponse;
import com.example.task_manager_backend.mappers.TaskMapper;
import com.example.task_manager_backend.models.task.Task;
import com.example.task_manager_backend.models.user.User;
import com.example.task_manager_backend.repositories.TaskRepository;
import com.example.task_manager_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private final TaskMapper taskMapper;

    @Override
    @Transactional
    public TaskResponse create(TaskRequest taskRequest, Long userId) {
        log.debug(
                "Creating task: userId={}, titleLength={}, descriptionPresent={}",
                userId,
                taskRequest.title().length(),
                taskRequest.description() != null && !taskRequest.description().isBlank()
        );

        User ownerReference = userRepository.getReferenceById(userId);

        Task task = new Task(
                taskRequest.title(),
                taskRequest.description(),
                ownerReference
        );

        Task createdTask = taskRepository.save(task);

        log.info(
                "Task created: taskId={}, userId={}, status={}",
                createdTask.getId(),
                userId,
                createdTask.getStatus()
        );

        return taskMapper.toResponse(createdTask);
    }


}
