package org.example.scheduler.client.exceptions;

public class TaskSummaryUnavailableException extends RuntimeException {
    public TaskSummaryUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
