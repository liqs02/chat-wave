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

import static org.mockito.ArgumentMatchers.any;
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
        when(
                authClient.getUserAuthentication("Bearer accessToken")
        ).thenReturn(new UserAuthentication());

        webTestClient.get().uri("/accounts/1/exist")
                .header("User-Authorization", "Bearer accessToken")
                .exchange()
                .expectStatus().isNotFound();
    }
}
