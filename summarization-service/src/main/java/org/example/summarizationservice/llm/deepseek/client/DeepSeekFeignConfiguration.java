package org.example.summarizationservice.llm.deepseek.client;

import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.example.summarizationservice.llm.deepseek.properties.DeepSeekProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;


public class DeepSeekFeignConfiguration {

    private static final long RETRY_INITIAL_DELAY_MILLIS = 500L;
    private static final long RETRY_MAX_DELAY_MILLIS = 1_000L;
    private static final int RETRY_MAX_ATTEMPTS = 2;

    private static final String HEADER_NAME_AUTHORIZATION = "Authorization";
    private static final String HEADER_VALUE_START_BEARER = "Bearer ";


    @Bean
    public RequestInterceptor deepSeekAuthorizationInterceptor(
            DeepSeekProperties properties
    ) {
        return requestTemplate -> requestTemplate.header(
                HEADER_NAME_AUTHORIZATION,
                HEADER_VALUE_START_BEARER + properties.apiKey()
        );
    }

    @Bean
    public Request.Options deepSeekRequestOptions(
            DeepSeekProperties properties
    ) {
        return new Request.Options(
                properties.connectionTimeout().toMillis(),
                TimeUnit.MILLISECONDS,
                properties.responseTimeout().toMillis(),
                TimeUnit.MILLISECONDS,
                true
        );
    }

    @Bean
    public Retryer deepSeekRetryer() {
        return new Retryer.Default(
                RETRY_INITIAL_DELAY_MILLIS,
                RETRY_MAX_DELAY_MILLIS,
                RETRY_MAX_ATTEMPTS
        );
    }

    @Bean
    public ErrorDecoder deepSeekErrorDecoder() {
        return new DeepSeekFeignErrorDecoder();
    }
}
