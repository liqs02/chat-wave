package com.chatwave.accountservice.exception;

import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApiExceptionHandler")
public class ApiExceptionHandlerTest extends ApiExceptionHandler {
    private final String exampleMessage = "[400] during [POST] to [http://micro-service/endpoint] [MicroService#method(param)]: [{\"message\":\"Valid message.\",\"status\":\"BAD_REQUEST\",\"timestamp\":\"2023-01-01T00:00:00\"}]";
    @Mock
    private FeignException feignException;

    @Nested
    @DisplayName("handleFeignException()")
    class HandleFeignException {
        @Test
        @DisplayName("should return default message from response message")
        public void t1() {
            when(
                    feignException.getMessage()
            ).thenReturn(exampleMessage);

            when(
                    feignException.status()
            ).thenReturn(400);

            var result = handleFeignException(feignException);
            var apiException = (ApiException) result.getBody();

            assertEquals("Valid message.", apiException.getMessage());
            assertEquals(HttpStatusCode.valueOf(400), result.getStatusCode());
        }

        @Test
        @DisplayName("should return message from basic message")
        public void t2() {
            when(
                    feignException.getMessage()
            ).thenReturn("Valid message.");

            when(
                    feignException.status()
            ).thenReturn(400);

            var result = handleFeignException(feignException);
            var apiException = (ApiException) result.getBody();

            assertEquals("Valid message.", apiException.getMessage());
            assertEquals(HttpStatusCode.valueOf(400), result.getStatusCode());
        }
    }
}
