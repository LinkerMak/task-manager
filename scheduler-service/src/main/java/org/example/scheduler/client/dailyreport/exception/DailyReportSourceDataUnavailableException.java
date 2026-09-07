package org.example.scheduler.client.dailyreport.exception;

public class DailyReportSourceDataUnavailableException extends RuntimeException {
    public DailyReportSourceDataUnavailableException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }}
