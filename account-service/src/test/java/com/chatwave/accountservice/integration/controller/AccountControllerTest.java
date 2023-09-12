package com.chatwave.accountservice.integration.controller;

import com.chatwave.accountservice.client.AuthClient;
import com.chatwave.accountservice.client.dto.AuthenticationResponse;
import com.chatwave.accountservice.client.dto.RegisterResponse;
import com.chatwave.accountservice.client.dto.TokenSet;
import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.dto.AccountResponse;
import com.chatwave.accountservice.domain.dto.CreateAccountRequest;
import com.chatwave.accountservice.domain.dto.PatchAccountRequest;
import com.chatwave.accountservice.repository.AccountRepository;
import com.chatwave.authclient.domain.UserAuthentication;
import com.chatwave.authclient.domain.UserAuthenticationDetails;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.HashMap;

import static com.chatwave.accountservice.utils.TestVariables.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
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
        account.setId(USER_ID);
        account.setDisplayName(DISPLAY_NAME);

        accountRepository.save(account);

        var userAuthentication = new UserAuthentication();
        userAuthentication.setUserId(account.getId());
        userAuthentication.setDetails(new UserAuthenticationDetails());

        when(
                authClient.getUserAuthentication(BEARER_TOKEN)
        ).thenReturn(userAuthentication);

        return account;
    }

    @Nested
    @DisplayName("POST /accounts")
    public class c1 {
        @Test
        @DisplayName("should create an account")
        public void t1() {
            when(
                    authClient.createUser(REGISTER_REQUEST)
            ).thenReturn(new RegisterResponse(USER_ID));

            when(
                    authClient.createSessions(CREATE_SESSION_REQUEST)
            ).thenReturn(new TokenSet(REFRESH_TOKEN, ACCESS_TOKEN));

            var tokenSet = webTestClient.post()
                    .uri("/accounts")
                    .bodyValue(new CreateAccountRequest(LOGIN_NAME, DISPLAY_NAME, PASSWORD))
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(TokenSet.class)
                    .returnResult().getResponseBody();

            assertNotNull(tokenSet);
            assertEquals(ACCESS_TOKEN, tokenSet.accessToken());
            assertEquals(REFRESH_TOKEN, tokenSet.refreshToken());

            var optionalAccount = accountRepository.findById(USER_ID);
            assertTrue(optionalAccount.isPresent());
        }
    }

    @Nested
    @DisplayName("POST /accounts/authenticate")
    public class c2 {
        @Test
        @DisplayName("should authenticate a user and return created session")
        public void t1() {
            when(
                    authClient.authenticateUser(AUTHENTICATION_REQUEST)
            ).thenReturn(new AuthenticationResponse(USER_ID));

            when(
                    authClient.createSessions(CREATE_SESSION_REQUEST)
            ).thenReturn(new TokenSet(REFRESH_TOKEN, ACCESS_TOKEN));

            createAndSaveAccount();

            var tokenSet = webTestClient.post()
                    .uri("/accounts/authenticate")
                    .bodyValue(new AuthenticationRequest(LOGIN_NAME, PASSWORD))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(TokenSet.class)
                    .returnResult().getResponseBody();

            assertNotNull(tokenSet);
            assertEquals(ACCESS_TOKEN, tokenSet.accessToken());
            assertEquals(REFRESH_TOKEN, tokenSet.refreshToken());
        }

        private record AuthenticationRequest(String loginName, String password) {}
    }

    @Nested
    @DisplayName("GET /accounts/{accountId}/showcase")
    public class c3 {
        @Test
        @DisplayName("should return information about user")
        public void t1() {
            var accountId = createAndSaveAccount().getId();

            var result = webTestClient.get()
                    .uri("/accounts/{id}/showcase", accountId)
                    .header("User-Authorization", BEARER_TOKEN)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(AccountResponse.class)
                    .returnResult().getResponseBody();

            assertNotNull(result);
            assertEquals(accountId, result.id());
            assertEquals(DISPLAY_NAME, result.displayName());
        }
    }

    @Nested
    @DisplayName("PATCH /accounts/{accountId}")
    public class c4 {
        private final String ENDPOINT = "/accounts/{accountId}";
        private Integer accountId;

        @BeforeEach
        void setUp() {
            accountId = createAndSaveAccount().getId();
        }

        @Test
        @DisplayName("should update user's displayName and password")
        public void t200() {
            webTestClient.patch()
                    .uri(ENDPOINT, USER_ID)
                    .bodyValue(new PatchAccountRequest(DISPLAY_NAME_2, PASSWORD_2))
                    .header("User-Authorization", BEARER_TOKEN)
                    .exchange()
                    .expectStatus().isOk();

            verify(
                    authClient, times(1)
            ).patchUser(1, PATCH_USER_REQUEST);

            var account = accountRepository.findById(USER_ID);
            assertTrue(account.isPresent());
            assertEquals(DISPLAY_NAME_2, account.get().getDisplayName());
        }

        @Test
        @DisplayName("should do not update displayName if feignClient will throw exception")
        public void t400() {
            var request = Request.create(Request.HttpMethod.PATCH, "auth-service", new HashMap<>(), null, new RequestTemplate());

            doThrow(new FeignException.BadRequest("", request, null, null))
                    .when(authClient)
                    .patchUser(accountId, PATCH_USER_REQUEST);

            webTestClient.patch()
                    .uri(ENDPOINT, accountId)
                    .bodyValue(new PatchAccountRequest(DISPLAY_NAME_2, PASSWORD_2))
                    .header("User-Authorization", BEARER_TOKEN)
                    .exchange()
                    .expectStatus().isBadRequest();

            var account = accountRepository.findById(accountId);
            assertTrue(account.isPresent());
            assertEquals(DISPLAY_NAME, account.get().getDisplayName());
        }

        @Test
        @DisplayName("should return 403 if user wants to update other user's password")
        public void t403() {
            webTestClient.patch()
                    .uri(ENDPOINT, accountId + 1)
                    .bodyValue(new PatchAccountRequest(PASSWORD, PASSWORD_2))
                    .header("User-Authorization", BEARER_TOKEN)
                    .exchange()
                    .expectStatus().isForbidden();
        }
    }
}
