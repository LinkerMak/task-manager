package org.example.summarizationservice.llm.deepseek;

import lombok.RequiredArgsConstructor;
import org.example.summarizationservice.llm.TaskSummaryGenerator;
import org.example.summarizationservice.llm.TaskSummaryPrompt;
import org.example.summarizationservice.llm.TaskSummaryPromptFactory;
import org.example.summarizationservice.llm.deepseek.dto.DeepSeekChatCompletionRequest;
import org.example.summarizationservice.llm.deepseek.dto.DeepSeekChatCompletionResponse;
import org.example.summarizationservice.llm.deepseek.exceptions.nonretryable.NonRetryableDeepSeekException;
import org.example.summarizationservice.llm.deepseek.exceptions.retryable.RetryableDeepSeekException;
import org.example.summarizationservice.llm.deepseek.properties.DeepSeekProperties;
import org.example.taskmanager.contracts.summary.TaskSummaryRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeepSeekTaskSummaryGenerator implements TaskSummaryGenerator {

    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    private static final String REQUEST_MESSAGE_ROLE_SYSTEM = "system";
    private static final String REQUEST_MESSAGE_ROLE_USER = "user";

    private final WebClient deepSeekWebClient;
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
                    = createResponse(deepSeekRequest);

            return extractSummaryText(response);
        } catch (WebClientRequestException e) {
            throw new RetryableDeepSeekException(
                    "DeepSeek API request failed due to a network error",
                    e
            );
        } catch (WebClientResponseException e) {
            HttpStatusCode statusCode = e.getStatusCode();

            String message = "DeepSeek API returned HTTP %s: %s"
                    .formatted(statusCode.value(), e.getResponseBodyAsString());

            if (statusCode.value() == 429 || statusCode.is5xxServerError()) {
                throw new RetryableDeepSeekException(message, e);
            }

            throw new NonRetryableDeepSeekException(
                    message,
                    e
            );
        }
    }

    private DeepSeekChatCompletionRequest createRequest(TaskSummaryPrompt prompt) {
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

    private DeepSeekChatCompletionResponse createResponse(DeepSeekChatCompletionRequest request) {
        return deepSeekWebClient.post()
                .uri(CHAT_COMPLETIONS_PATH)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(DeepSeekChatCompletionResponse.class)
                .block();
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

