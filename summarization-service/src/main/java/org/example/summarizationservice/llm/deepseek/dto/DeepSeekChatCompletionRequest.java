package org.example.summarizationservice.llm.deepseek.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DeepSeekChatCompletionRequest(
        String model,
        List<Message> messages,
        double temperature,

        @JsonProperty("max_tokens")
        int maxTokens,
        boolean stream
) {

    public record Message(
            String role,
            String content
    ){}
}
