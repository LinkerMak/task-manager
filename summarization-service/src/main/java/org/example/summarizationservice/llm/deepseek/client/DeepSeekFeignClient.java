package org.example.summarizationservice.llm.deepseek.client;

import org.example.summarizationservice.llm.deepseek.dto.DeepSeekChatCompletionRequest;
import org.example.summarizationservice.llm.deepseek.dto.DeepSeekChatCompletionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "deepseek-client",
        url = "${llm.deepseek.base-url}",
        configuration = DeepSeekFeignConfiguration.class
)
public interface DeepSeekFeignClient {

    @PostMapping(
            value = "/chat/completions",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    DeepSeekChatCompletionResponse createChatCompletion(
            @RequestBody DeepSeekChatCompletionRequest request
    );
}
