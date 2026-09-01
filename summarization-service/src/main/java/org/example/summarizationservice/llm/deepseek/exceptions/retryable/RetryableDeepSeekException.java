package org.example.summarizationservice.llm.deepseek.exceptions.retryable;

import org.example.summarizationservice.llm.deepseek.exceptions.DeepSeekException;

public final class RetryableDeepSeekException extends DeepSeekException {

    public RetryableDeepSeekException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetryableDeepSeekException(String message) {
        super(message);
    }
}
