package org.example.taskmanager.contracts.dailyreport;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.example.taskmanager.contracts.task.TaskSnapshot;

import java.util.List;

public record DailyReportUserData(
        @NotNull
        Long userId,

        @NotNull
        @Email
        String email,

        @NotNull
        List<@Valid TaskSnapshot> tasks
) {
}
