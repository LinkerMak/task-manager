package org.example.summarizationservice.llm;

public record TaskSummaryPrompt(
        String systemMessage,
        String userMessage
) {
}
