package com.chatwave.accountservice.integration.controller;

import com.chatwave.accountservice.client.AuthClient;
import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.dto.*;
import com.chatwave.accountservice.repository.AccountRepository;
import com.chatwave.authclient.domain.UserAuthentication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
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

    private Account createAndSaveAccount() {
        var account = new Account();
        account.setId(1);
        account.setLoginName("loginName");
        account.setDisplayName("displayName");

        accountRepository.save(account);
        return account;
    }

    @Nested
    @DisplayName("POST /accounts")
    public class createAccount {
        @Test
        @DisplayName("should create an account")
        public void t1() {
            when(
                    authClient.createUser( new CreateUserRequest(any(), "Pass1234"))
            ).thenReturn(new TokenSet("refreshToken", "accessToken"));

            var tokenSet = webTestClient.post()
                    .uri("/accounts")
                    .bodyValue(new CreateAccountRequest("loginName", "display","Pass1234"))
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

    @Nested
    @DisplayName("POST /accounts/authenticate")
    public class authenticateAccount {
        @Test
        @DisplayName("should authenticate a user")
        public void t1() {
            when(
                    authClient.authenticateUser( new AuthenticateUserRequest(any(), "Pass1234") )
            ).thenReturn(new TokenSet("refreshToken", "accessToken"));

            createAndSaveAccount();

            var tokenSet = webTestClient.post()
                    .uri("/accounts/authenticate")
                    .bodyValue(new AuthenticateAccountRequest("loginName","Pass1234"))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(TokenSet.class)
                    .returnResult().getResponseBody();

            assertNotNull(tokenSet);
            assertEquals("accessToken", tokenSet.accessToken());
            assertEquals("refreshToken", tokenSet.refreshToken());
        }
    }
}
