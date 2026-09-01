package org.example.summarizationservice.llm.deepseek.exceptions;

public abstract class DeepSeekException extends RuntimeException {

    protected DeepSeekException(String message, Throwable cause) {
        super(message, cause);
    }

    protected DeepSeekException(String message) {
        super(message);
    }
}