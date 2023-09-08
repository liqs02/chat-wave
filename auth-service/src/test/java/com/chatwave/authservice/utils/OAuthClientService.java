package com.chatwave.authservice.utils;

import lombok.AllArgsConstructor;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@AllArgsConstructor
public class OAuthClientService {
    private final WebTestClient webTestClient;

    public String getAccessToken() {
        var formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", "account-service");
        formData.add("client_secret", "secret");
        formData.add("scope", "openid server");

        var result = webTestClient.post()
                .uri("/oauth2/token")
                .contentType(APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class).returnResult().getResponseBody();

        if(result == null)
            fail("webTestClient could not get client's accessToken");

        return (String) result.get("access_token");
    }

    public String getAuthHeader() {
        return "Bearer " + this.getAccessToken();
    }
}
