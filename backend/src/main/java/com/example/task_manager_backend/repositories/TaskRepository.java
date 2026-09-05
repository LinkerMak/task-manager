package com.example.task_manager_backend.repositories;

import com.example.task_manager_backend.dto.repository.dailyreport.DailyReportTaskRow;
import com.example.task_manager_backend.models.task.Task;
import com.example.task_manager_backend.models.task.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findAllByOwner_Id(Long ownerId, Pageable pageable);

    Optional<Task> findByIdAndOwner_Id(Long taskId, Long ownerId);

    @Query("""
            select new com.example.task_manager_backend.dto.repository.dailyreport.DailyReportTaskRow(
                task.owner.id,
                task.owner.email,
                task.id,
                task.title,
                task.description,
                task.status,
                task.completedAt
            )
            from Task task
            where task.status = :todoStatus
               or (
                   task.status = :doneStatus
                   and task.completedAt >= :periodStart
                   and task.completedAt < :periodEnd
               )
            order by task.owner.id, task.id
            """)
    List<DailyReportTaskRow> findDailyReportTaskRows(
            @Param("todoStatus") TaskStatus todoStatus,
            @Param("doneStatus") TaskStatus doneStatus,
            @Param("periodStart") OffsetDateTime periodStart,
            @Param("periodEnd") OffsetDateTime periodEnd
    );
}
