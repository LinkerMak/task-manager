package org.example.summarizationservice.llm.deepseek.dto;

import java.util.List;

public record DeepSeekChatCompletionResponse(
        List<Choice> choices
) {
    public record Choice(
            DeepSeekChatCompletionRequest.Message message
    ){}
}
