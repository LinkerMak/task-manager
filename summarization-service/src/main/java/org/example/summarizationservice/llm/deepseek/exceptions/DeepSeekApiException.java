package org.example.summarizationservice.llm.deepseek.exceptions;

public class DeepSeekApiException extends RuntimeException {

    private final int statusCode;

    public DeepSeekApiException(int statusCode, String responseBody) {
        super("DeepSeek API returned HTTP %d: %s".formatted(statusCode, responseBody));
        this.statusCode = statusCode;
    }

    public DeepSeekApiException(
            int statusCode,
            String responseBody,
            Throwable cause
    ) {
        super("DeepSeek API returned HTTP %d: %s".formatted(statusCode, responseBody), cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
