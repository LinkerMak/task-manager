package org.example.scheduler.client.tasksummary;

import org.example.taskmanager.contracts.summary.TaskSummaryRequest;
import org.example.taskmanager.contracts.summary.TaskSummaryResponse;

public interface TaskSummaryClient {
    TaskSummaryResponse summarize(TaskSummaryRequest request);
}
