package com.example.task_manager_backend.models.task;

import com.example.task_manager_backend.models.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "tasks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 255)
    private TaskStatus status;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public Task(String title, String description, User owner) {
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.status = TaskStatus.TODO;
    }

    public void updateDetails(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void markAsDone() {
        if (status == TaskStatus.DONE) {
            return;
        }

        this.status = TaskStatus.DONE;
        this.completedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public void reopen() {
        if (status == TaskStatus.TODO) {
            return;
        }

        this.status = TaskStatus.TODO;
        this.completedAt = null;
    }
}
