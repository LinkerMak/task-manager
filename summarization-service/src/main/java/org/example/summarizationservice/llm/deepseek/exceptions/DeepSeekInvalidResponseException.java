package org.example.summarizationservice.llm.deepseek.exceptions;

public class DeepSeekInvalidResponseException extends RuntimeException {
    public DeepSeekInvalidResponseException(String message) {
        super(message);
    }
}
