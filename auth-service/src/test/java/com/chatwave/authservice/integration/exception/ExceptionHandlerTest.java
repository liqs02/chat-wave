package com.chatwave.authservice.integration.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("ExceptionHandler integration test")
public class ExceptionHandlerTest {
    @Autowired
    protected WebTestClient webTestClient;

    @Test
    @DisplayName("test that ExceptionHandler is implemented correctly")
    public void t1() {
        webTestClient.put()
                .uri("/sessions/refresh")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(METHOD_NOT_ALLOWED)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Method 'PUT' is not supported.")
                .jsonPath("$.status").isEqualTo("METHOD_NOT_ALLOWED")
                .jsonPath("$.timestamp").isNotEmpty();
    }
}
