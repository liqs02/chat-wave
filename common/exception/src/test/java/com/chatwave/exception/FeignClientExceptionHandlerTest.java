package com.chatwave.exception;

import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("handleFeignException()")
public class FeignClientExceptionHandlerTest extends ApiExceptionHandler {
    @Mock
    private FeignException feignException;

    private final String EXCEPTION_WITH_MESSAGE = "[400] during [POST] to [http://micro-service/endpoint] [MicroService#method(param)]: [{\"message\":\"Valid message.\",\"status\":\"BAD_REQUEST\",\"timestamp\":\"2023-01-01T00:00:00\"}]";
    private final String EXCEPTION_WITHOUT_MESSAGE = "[400] during [POST] to [http://micro-service/endpoint] [MicroService#method(param)]: [{\"status\":\"BAD_REQUEST\",\"timestamp\":\"2023-01-01T00:00:00\"}]";

    @Test
    @DisplayName("should return apiException with message")
    public void t1() {
        when(
                feignException.getMessage()
        ).thenReturn(EXCEPTION_WITH_MESSAGE);

        when(
                feignException.status()
        ).thenReturn(400);

        var result = handleFeignException(feignException);
        var apiException = result.getBody();

        assertNotNull(apiException);
        assertEquals("Valid message.", apiException.getMessage());
        assertEquals(HttpStatusCode.valueOf(400), result.getStatusCode());
    }

    @Test
    @DisplayName("should return apiException with no message")
    public void t2() {
        when(
                feignException.getMessage()
        ).thenReturn(EXCEPTION_WITHOUT_MESSAGE);

        when(
                feignException.status()
        ).thenReturn(400);

        var result = handleFeignException(feignException);
        var apiException = result.getBody();

        assertNotNull(apiException);
        assertEquals("", apiException.getMessage());
        assertEquals(HttpStatusCode.valueOf(400), result.getStatusCode());
    }
}
