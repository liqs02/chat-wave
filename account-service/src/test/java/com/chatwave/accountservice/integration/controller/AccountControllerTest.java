package com.chatwave.accountservice.integration.controller;

import com.chatwave.accountservice.client.AuthClient;
import com.chatwave.accountservice.domain.dto.CreateAccountRequest;
import com.chatwave.accountservice.domain.dto.CreateUserRequest;
import com.chatwave.accountservice.domain.dto.TokenSet;
import com.chatwave.accountservice.repository.AccountRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("AccountController integration tests")
public class AccountControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private AccountRepository accountRepository;
    @MockBean
    private AuthClient authClient;

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /accounts")
    public class createAccount {
        @Test
        @DisplayName("should create an account")
        public void t1() {
            when(
                    authClient.createUser( new CreateUserRequest(1, "Pass1234"))
            ).thenReturn(new TokenSet("refreshToken", "accessToken"));


            var createAccountRequest = new CreateAccountRequest("loginName", "display","Pass1234");
            var tokenSet = webTestClient.post()
                    .uri("/accounts")
                    .bodyValue(createAccountRequest)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(TokenSet.class)
                    .returnResult().getResponseBody();

            assertNotNull(tokenSet);
            assertEquals("accessToken", tokenSet.accessToken());
            assertEquals("refreshToken", tokenSet.refreshToken());

            var optionalAccount = accountRepository.findByLoginName("loginName");
            assertTrue(optionalAccount.isPresent());
        }
    }

}
