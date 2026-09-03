package org.example.summarizationservice.service;

import lombok.RequiredArgsConstructor;
import org.example.summarizationservice.llm.TaskSummaryGenerator;
import org.example.taskmanager.contracts.summary.TaskSummaryRequest;
import org.example.taskmanager.contracts.summary.TaskSummaryResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenerateTaskSummaryServiceImpl implements GenerateTaskSummaryService {

    private final TaskSummaryGenerator taskSummaryGenerator;

    @Override
    public TaskSummaryResponse generate(TaskSummaryRequest request) {
        String summaryText = taskSummaryGenerator.generate(request);

        return new TaskSummaryResponse(
                request.requestId(),
                summaryText
        );
    }

}
