package org.example.summarizationservice.usecase;

import org.example.taskmanager.contracts.summary.TaskSummaryRequest;
import org.example.taskmanager.contracts.summary.TaskSummaryResponse;

public interface GenerateTaskSummaryUseCase {
    TaskSummaryResponse generate(TaskSummaryRequest request);
}
