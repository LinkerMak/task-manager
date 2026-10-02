package org.example.scheduler.producer.tasksummary;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager.contracts.summary.TaskSummaryRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaTaskSummaryRequestProducer implements TaskSummaryRequestProducer{
    @Override
    public void publish(TaskSummaryRequest taskSummaryRequest) {

    }
}
