package com.example.task_manager_backend.services.task;

import com.example.task_manager_backend.dto.web.pages.PagedResponse;
import com.example.task_manager_backend.dto.web.task.TaskRequest;
import com.example.task_manager_backend.dto.web.task.TaskResponse;
import com.example.task_manager_backend.dto.web.task.update.UpdateDescriptionRequest;
import com.example.task_manager_backend.dto.web.task.update.UpdateTaskStatusRequest;
import com.example.task_manager_backend.dto.web.task.update.UpdateTitleRequest;
import com.example.task_manager_backend.exceptions.resource.ResourceNotFoundException;
import com.example.task_manager_backend.mappers.PageMapper;
import com.example.task_manager_backend.mappers.TaskMapper;
import com.example.task_manager_backend.models.task.Task;
import com.example.task_manager_backend.models.task.TaskStatus;
import com.example.task_manager_backend.models.user.User;
import com.example.task_manager_backend.repositories.TaskRepository;
import com.example.task_manager_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private final TaskMapper taskMapper;

    @Override
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

    @Override
    public TaskResponse updateDescription(Long taskId, UpdateDescriptionRequest descriptionRequest, Long userId) {
        log.debug("Updating task description: taskId={}, userId={}, descriptionRequest={}", taskId, userId, descriptionRequest);

        Task task = taskRepository.findByIdAndOwner_Id(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found by id=" + taskId + " for user with id=" + userId)
                );

        task.updateDescription(descriptionRequest.description());

        log.info(
                "Task description updated: taskId={}, userId={}, description={}",
                taskId,
                userId,
                task.getDescription());

        return taskMapper.toResponse(task);
    }

    @Override
    public TaskResponse updateTitle(Long taskId, UpdateTitleRequest titleRequest, Long userId) {
        log.debug("Updating task title: taskId={}, userId={}, titleRequest={}", taskId, userId, titleRequest);

        Task task = taskRepository.findByIdAndOwner_Id(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found by id=" + taskId + " for user with id=" + userId)
                );

        task.updateTitle(titleRequest.title());

        log.info(
                "Task title updated: taskId={}, userId={}, title={}",
                taskId,
                userId,
                task.getTitle());

        return taskMapper.toResponse(task);
    }

    @Override
    public TaskResponse changeStatus(Long taskId, UpdateTaskStatusRequest updateTaskStatusRequest, Long userId) {
        log.debug("Changing task status: taskId={}, userId={}, statusRequest={}", taskId, userId, updateTaskStatusRequest);

        TaskStatus status = updateTaskStatusRequest.status();

        Task task = taskRepository.findByIdAndOwner_Id(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found by id=" + taskId + " for user with id=" + userId)
                );

        task.changeStatus(status);

        log.info(
                "Task status changed: taskId={}, userId={}, status={}, completedAt={}",
                taskId,
                userId,
                status,
                task.getCompletedAt()
        );

        return taskMapper.toResponse(task);
    }

    @Override
    public void delete(Long taskId, Long userId) {
        log.debug("Deleting task: taskId={}, userId={}", taskId, userId);

        Task task = taskRepository.findByIdAndOwner_Id(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found by id=" + taskId + " for user with id=" + userId)
                );

        taskRepository.delete(task);

        log.info("Task deleted: taskId={}, userId={}", taskId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TaskResponse> getAllTasksForUser(Long userId, Pageable pageable) {
        log.debug(
                "Getting tasks for user: userId={}, page={}, size={}",
                userId,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<TaskResponse> taskResponses = taskRepository.findAllByOwner_Id(userId, pageable)
                .map((task) -> taskMapper.toResponse(task));

        log.debug(
                "Tasks retrieved: userId={}, tasksOnPage={}, totalElements={}",
                userId,
                taskResponses.getNumberOfElements(),
                taskResponses.getTotalElements()
        );

        return PageMapper.from(taskResponses);
    }

}
