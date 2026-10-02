package org.example.summarizationservice.llm;

import org.example.taskmanager.contracts.summary.TaskSummaryRequest;

public interface TaskSummaryGenerator {
    String generate(TaskSummaryRequest request);
}
