package com.chatwave.authservice.integration.securityConfig;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("SecurityConfig authentication tests")
public class ClientAuthTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("test clients' authorization should return client's token")
    public void t1() {
        var formData = new LinkedMultiValueMap<String, String>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", "account-service");
        formData.add("client_secret", "secret");
        formData.add("scope", "openid server");

         webTestClient.post()
                .uri("/oauth2/token")
                .contentType(APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.access_token").isNotEmpty()
                .jsonPath("$.scope").isEqualTo("server openid")
                .jsonPath("$.token_type").isEqualTo("Bearer")
                .jsonPath("$.expires_in").isEqualTo(299);
    }

}
