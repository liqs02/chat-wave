package com.chatwave.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PDF;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
public class ApiExceptionHandlerTest {
    @Autowired
    protected WebTestClient webTestClient;

    @Test
    @DisplayName("should throw correct exception if not supported contentType is provided")
    public void t1() {
        webTestClient.get()
                .uri("/")
                .exchange()
                .expectStatus().isEqualTo(UNSUPPORTED_MEDIA_TYPE)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Content-Type 'null' is not supported.")
                .jsonPath("$.status").isEqualTo("UNSUPPORTED_MEDIA_TYPE")
                .jsonPath("$.timestamp").isNotEmpty();
    }

    @Test
    @DisplayName("should throw correct exception if not supported accept mediaType is provided")
    public void t2() {
        webTestClient.get()
                .uri("/")
                .header("Content-Type", "application/json")
                .accept(APPLICATION_PDF)
                .exchange()
                .expectStatus().isEqualTo(NOT_ACCEPTABLE)
                .expectBody()
                .jsonPath("$.message").isEqualTo("No acceptable representation")
                .jsonPath("$.status").isEqualTo("NOT_ACCEPTABLE")
                .jsonPath("$.timestamp").isNotEmpty();
    }

    @Test
    @DisplayName("should throw correct exception if method is not allowed")
    public void t3() {
        webTestClient.post()
                .uri("/")
                .header("Content-Type", "application/json")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(METHOD_NOT_ALLOWED)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Method 'POST' is not supported.")
                .jsonPath("$.status").isEqualTo("METHOD_NOT_ALLOWED")
                .jsonPath("$.timestamp").isNotEmpty();
    }
}
