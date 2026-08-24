package com.example.task_manager_backend.repositories;

import com.example.task_manager_backend.models.task.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findAllByOwner_Id(Long ownerId, Pageable pageable);

    Optional<Task> findByIdAndOwner_Id(Long taskId, Long ownerId);

}
