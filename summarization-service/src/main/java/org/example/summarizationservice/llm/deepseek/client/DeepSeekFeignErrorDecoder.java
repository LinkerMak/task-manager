package org.example.summarizationservice.llm.deepseek.client;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;

import java.util.Date;

public class DeepSeekFeignErrorDecoder implements ErrorDecoder {

    private static final int TOO_MANY_REQUESTS_STATUS = 429;

    private static final int SERVER_ERROR_STATUSES = 500;

    private static final String RETRYABLE_MESSAGE = "DeepSeek API returned retryable HTTP status: ";

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();

        if (status == TOO_MANY_REQUESTS_STATUS || status >= SERVER_ERROR_STATUSES) {
            return new RetryableException(
                    status,
                    RETRYABLE_MESSAGE + status,
                    response.request().httpMethod(),
                    null,
                    new Date(),
                    response.request()
            );
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }
}
