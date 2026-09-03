package org.example.summarizationservice.llm.deepseek.generator;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.example.summarizationservice.llm.TaskSummaryGenerator;
import org.example.summarizationservice.llm.TaskSummaryPrompt;
import org.example.summarizationservice.llm.TaskSummaryPromptFactory;
import org.example.summarizationservice.llm.deepseek.client.DeepSeekFeignClient;
import org.example.summarizationservice.llm.deepseek.dto.DeepSeekChatCompletionRequest;
import org.example.summarizationservice.llm.deepseek.dto.DeepSeekChatCompletionResponse;
import org.example.summarizationservice.llm.deepseek.exceptions.nonretryable.NonRetryableDeepSeekException;
import org.example.summarizationservice.llm.deepseek.exceptions.retryable.RetryableDeepSeekException;
import org.example.summarizationservice.llm.deepseek.properties.DeepSeekProperties;
import org.example.taskmanager.contracts.summary.TaskSummaryRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeepSeekTaskSummaryGenerator implements TaskSummaryGenerator {

    private static final String REQUEST_MESSAGE_ROLE_SYSTEM = "system";
    private static final String REQUEST_MESSAGE_ROLE_USER = "user";

    private final DeepSeekFeignClient deepSeekFeignClient;
    private final DeepSeekProperties deepSeekProperties;
    private final TaskSummaryPromptFactory promptFactory;

    @Override
    public String generate(TaskSummaryRequest request) {
        TaskSummaryPrompt prompt
                = promptFactory.create(request);

        DeepSeekChatCompletionRequest deepSeekRequest
                = createRequest(prompt);

        try {
            DeepSeekChatCompletionResponse response
                    = deepSeekFeignClient.createChatCompletion(deepSeekRequest);

            return extractSummaryText(response);
        } catch (RetryableException e) {
            throw new RetryableDeepSeekException(
                    "DeepSeek API request failed after Feign retry attempts",
                    e
            );
        } catch (FeignException e) {
            throw new NonRetryableDeepSeekException(
                    "DeepSeek API returned HTTP %s: %s".formatted(
                            e.status(),
                            e.contentUTF8()),
                    e
            );
        }
    }

    private DeepSeekChatCompletionRequest createRequest(
            TaskSummaryPrompt prompt
    ) {
        return new DeepSeekChatCompletionRequest(
                deepSeekProperties.model(),
                List.of(
                        new DeepSeekChatCompletionRequest.Message(
                                REQUEST_MESSAGE_ROLE_SYSTEM,
                                prompt.systemMessage()
                        ),
                        new DeepSeekChatCompletionRequest.Message(
                                REQUEST_MESSAGE_ROLE_USER,
                                prompt.userMessage()
                        )
                ),
                deepSeekProperties.temperature(),
                deepSeekProperties.maxOutputTokens(),
                false
        );
    }

    private String extractSummaryText(DeepSeekChatCompletionResponse response) {
        if (response == null
                || response.choices() == null
                || response.choices().isEmpty()
                || response.choices().getFirst().message() == null
                || response.choices().getFirst().message().content() == null
                || response.choices().getFirst().message().content().isBlank()) {

            throw new NonRetryableDeepSeekException(
                    "DeepSeek API returned an empty or malformed completion"
            );
        }

        return response.choices()
                .getFirst()
                .message()
                .content()
                .strip();
    }
}

