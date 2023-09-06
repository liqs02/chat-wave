package com.chatwave.accountservice.integration;

import com.chatwave.accountservice.client.AuthClient;
import com.chatwave.authclient.domain.UserAuthentication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
public class UserAuthorizationTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private AuthClient authClient;

    @Test
    @DisplayName("should authorize a user by provided accessToken in header")
    public void t1() {
        var userAuthentication = new UserAuthentication();
        userAuthentication.setUserId(1);
        userAuthentication.setCredentials("accessToken");

        when(
                authClient.getUserAuthentication("Bearer accessToken")
        ).thenReturn(userAuthentication);

        webTestClient.get().uri("/accounts/1/showcase")
                .header("Content-type", APPLICATION_JSON)
                .header("User-Authorization", "Bearer accessToken")
                .exchange()
                .expectStatus().isNotFound();
    }
}
