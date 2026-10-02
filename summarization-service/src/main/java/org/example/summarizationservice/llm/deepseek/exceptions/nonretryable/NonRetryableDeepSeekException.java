package org.example.summarizationservice.llm.deepseek.exceptions.nonretryable;

import org.example.summarizationservice.llm.deepseek.exceptions.DeepSeekException;

public final class NonRetryableDeepSeekException extends DeepSeekException {

    public NonRetryableDeepSeekException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonRetryableDeepSeekException(String message) {
        super(message);
    }
}