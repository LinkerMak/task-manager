package org.example.scheduler.producer.tasksummary;

import org.example.taskmanager.contracts.summary.TaskSummaryRequest;

public interface TaskSummaryRequestProducer {
    void publish(TaskSummaryRequest taskSummaryRequest);
}
