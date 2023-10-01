package com.chatwave.accountservice.integration;

import com.chatwave.accountservice.utils.ContainersConfig;
import com.chatwave.authclient.domain.UserAuthentication;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.chatwave.accountservice.utils.JsonUtils.toJson;
import static com.chatwave.accountservice.utils.TestVariables.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Import(ContainersConfig.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
public class UserAuthorizationTest {
    @Autowired
    private WebTestClient webTestClient;
    private static WireMockServer wireMockServer;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(
                WireMockConfiguration.wireMockConfig().dynamicPort()
        );

        wireMockServer.start();
    }

    @DynamicPropertySource
    public static void overrideWebClientBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("auth-service.url", wireMockServer::baseUrl);
    }

    @AfterAll
    public static void stopWireMock() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("should authorize a user by provided accessToken in header")
    public void t1() {
        var userAuthentication = new UserAuthentication();
        userAuthentication.setUserId(USER_ID);
        userAuthentication.setCredentials(ACCESS_TOKEN);

        wireMockServer.stubFor(
                get("/sessions/authentication")
                        .withHeader("User-Authorization", equalTo(BEARER_TOKEN))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", APPLICATION_JSON)
                                        .withBody(toJson(userAuthentication)))
        );

        webTestClient.get().uri("/accounts/1/showcase")
                .header("Content-type", APPLICATION_JSON)
                .header("User-Authorization", BEARER_TOKEN)
                .exchange()
                .expectStatus().isNotFound();
    }
}
