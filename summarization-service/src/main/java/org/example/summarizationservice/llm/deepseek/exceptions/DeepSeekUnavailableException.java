package org.example.summarizationservice.llm.deepseek.exceptions;

public class DeepSeekUnavailableException extends RuntimeException {
    public DeepSeekUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
