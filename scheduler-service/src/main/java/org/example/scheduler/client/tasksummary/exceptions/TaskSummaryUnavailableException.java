package org.example.scheduler.client.tasksummary.exceptions;

public class TaskSummaryUnavailableException extends RuntimeException {
    public TaskSummaryUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
