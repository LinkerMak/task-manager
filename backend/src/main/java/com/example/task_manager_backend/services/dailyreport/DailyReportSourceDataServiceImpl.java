package com.example.task_manager_backend.services.dailyreport;

import com.example.task_manager_backend.dto.repository.dailyreport.DailyReportTaskRow;
import com.example.task_manager_backend.models.task.TaskStatus;
import com.example.task_manager_backend.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanager.contracts.dailyreport.DailyReportSourceDataResponse;
import org.example.taskmanager.contracts.dailyreport.DailyReportUserData;
import org.example.taskmanager.contracts.task.TaskSnapshot;
import org.example.taskmanager.contracts.task.TaskSnapshotStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyReportSourceDataServiceImpl implements DailyReportSourceDataService {

    private final TaskRepository taskRepository;

    @Override
    public DailyReportSourceDataResponse getDataSource(OffsetDateTime periodStart, OffsetDateTime periodEnd) {
        validatePeriod(periodStart, periodEnd);

        List<DailyReportTaskRow> taskRows = taskRepository.findDailyReportTaskRows(
                TaskStatus.TODO,
                TaskStatus.DONE,
                periodStart,
                periodEnd
        );

        List<DailyReportUserData> userResponses =
                groupTasksByUser(taskRows);

        log.info(
                "Daily report source data retrieved: periodStart={}, periodEnd={}, usersCount={}, tasksCount={}",
                periodStart,
                periodEnd,
                userResponses.size(),
                taskRows.size()
        );

        return new DailyReportSourceDataResponse(
                periodStart,
                periodEnd,
                userResponses
        );
    }

    List<DailyReportUserData> groupTasksByUser(List<DailyReportTaskRow> taskRows) {
        Map<Long, DailyReportUserAccumulator> usersById =
                new LinkedHashMap<>();

        for (DailyReportTaskRow taskRow : taskRows) {
            DailyReportUserAccumulator user =
                    usersById.computeIfAbsent(
                            taskRow.userId(),
                            ignored -> new DailyReportUserAccumulator(
                                    taskRow.userId(),
                                    taskRow.email()
                            )
                    );

            user.tasks().add(
                    new TaskSnapshot(
                            taskRow.taskId(),
                            taskRow.title(),
                            taskRow.description(),
                            toTaskSnapshotStatus(taskRow.status()),
                            taskRow.completedAt()
                    )
            );
        }

        return usersById.values().stream()
                .map(DailyReportUserAccumulator::toResponse)
                .toList();
    }

    private record DailyReportUserAccumulator(
            Long userId,
            String email,
            List<TaskSnapshot> tasks
    ) {
        private DailyReportUserAccumulator(
                Long userId,
                String email
        ) {
            this(userId, email, new ArrayList<>());
        }

        private DailyReportUserData toResponse() {
            return new DailyReportUserData(
                    userId,
                    email,
                    List.copyOf(tasks)
            );
        }
    }

    private TaskSnapshotStatus toTaskSnapshotStatus(
            TaskStatus status
    ) {
        return switch (status) {
            case TODO -> TaskSnapshotStatus.TODO;
            case DONE -> TaskSnapshotStatus.DONE;
        };
    }

    private void validatePeriod(OffsetDateTime periodStart, OffsetDateTime periodEnd) {
        if (!periodStart.isBefore(periodEnd)) {
            throw new IllegalArgumentException(
                    "Period start must be before period end"
            );
        }
    }
}
