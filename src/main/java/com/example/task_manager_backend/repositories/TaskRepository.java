package com.example.task_manager_backend.repositories;

import com.example.task_manager_backend.models.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByOwner_IdOrderByIdDesc(Long ownerId);

    Optional<Task> findByIdAndOwner_Id(Long taskId, Long ownerId);

}
