package org.example.summarizationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.summarizationservice.llm.TaskSummaryGenerator;
import org.example.taskmanager.contracts.summary.TaskSummaryRequest;
import org.example.taskmanager.contracts.summary.TaskSummaryResponse;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateTaskSummaryServiceImpl implements GenerateTaskSummaryService {

    private final TaskSummaryGenerator taskSummaryGenerator;

    @Override
    public TaskSummaryResponse generate(TaskSummaryRequest request) {
        String summaryText = taskSummaryGenerator.generate(request);

        TaskSummaryResponse response = new TaskSummaryResponse(
                request.requestId(),
                summaryText
        );

        log.info(
                "Task summary generation completed: requestId={}, summaryLength={}",
                response.requestId(),
                response.summaryText().length()
        );

        return response;
    }

}
