package org.example.scheduler.client;

import org.example.taskmanager.contracts.summary.TaskSummaryRequest;
import org.example.taskmanager.contracts.summary.TaskSummaryResponse;

public interface TaskSummaryClient {
    TaskSummaryResponse summarize(TaskSummaryRequest request);
}
