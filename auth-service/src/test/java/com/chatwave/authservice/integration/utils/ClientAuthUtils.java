package com.chatwave.authservice.integration.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;

import java.rmi.UnexpectedException;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ClientAuthUtils {
    @Autowired
    protected WebTestClient webTestClient;

    protected String getAccessToken() throws UnexpectedException {
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
            throw new UnexpectedException("Test could not take client's accessToken.");
        else
            return (String) result.get("access_token");
    }

    protected String getAuthHeader() throws UnexpectedException {
        return "Bearer " + this.getAccessToken();
    }
}
