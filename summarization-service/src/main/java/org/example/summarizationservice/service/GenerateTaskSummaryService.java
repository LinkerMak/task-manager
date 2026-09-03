package org.example.summarizationservice.service;

import org.example.taskmanager.contracts.summary.TaskSummaryRequest;
import org.example.taskmanager.contracts.summary.TaskSummaryResponse;

public interface GenerateTaskSummaryService {
    TaskSummaryResponse generate(TaskSummaryRequest request);
}
