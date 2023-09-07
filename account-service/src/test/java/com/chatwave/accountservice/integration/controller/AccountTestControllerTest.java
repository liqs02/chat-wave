package com.chatwave.accountservice.integration.controller;

import com.chatwave.accountservice.client.AuthClient;
import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.dto.*;
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

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("AccountController integration tests")
public class AccountTestControllerTest {
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
        account.setLoginName("loginName");
        account.setDisplayName("displayName");

        accountRepository.save(account);

        var userAuthentication = new UserAuthentication();
        userAuthentication.setUserId(account.getId());
        userAuthentication.setDetails(new UserAuthenticationDetails());

        when(
                authClient.getUserAuthentication("Bearer accessToken")
        ).thenReturn(userAuthentication);

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

    @Nested
    @DisplayName("GET /accounts/{accountId}/exist")
    public class doesAccountExist {
        // todo : add test (client authorization)
    }

    @Nested
    @DisplayName("GET /accounts/{accountId}/showcase")
    public class getAccountShowcase {
        @Test
        @DisplayName("should return information about user")
        public void t1() {
            var accountId = createAndSaveAccount().getId();

            var result = webTestClient.get()
                    .uri("/accounts/{id}/showcase", accountId)
                    .header("Content-type", APPLICATION_JSON)
                    .header("User-Authorization", "Bearer accessToken")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(AccountShowcase.class)
                    .returnResult().getResponseBody();

            assertNotNull(result);
            assertEquals(accountId, result.id());
            assertEquals("displayName", result.displayName());
        }
    }

    @Nested
    @DisplayName("PATCH /accounts/{accountId}")
    public class patchAccountPassword {
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
                    .uri(ENDPOINT, accountId)
                    .bodyValue(new PatchAccountRequest("newName", "Pass1234", "New12345"))
                    .header("User-Authorization", "Bearer accessToken")
                    .exchange()
                    .expectStatus().isOk();

            verify(
                    authClient, times(1)
            ).patchUser(1, new PatchUserRequest("Pass1234", "New12345"));

            var account = accountRepository.findById(accountId);
            assertTrue(account.isPresent());
            assertEquals("newName", account.get().getDisplayName());
        }

        @Test
        @DisplayName("should do not update displayName if feignClient will throw exception")
        public void t400() {
            var request = Request.create(Request.HttpMethod.PATCH, "auth-service", new HashMap<>(), null, new RequestTemplate());

            doThrow(new FeignException.BadRequest("Message", request, null, null))
                    .when(authClient)
                    .patchUser(accountId, new PatchUserRequest("Pass1234", "New12345"));

            webTestClient.patch()
                    .uri(ENDPOINT, accountId)
                    .bodyValue(new PatchAccountRequest("newName","Pass1234", "New12345"))
                    .header("User-Authorization", "Bearer accessToken")
                    .exchange()
                    .expectStatus().isBadRequest();

            var account = accountRepository.findById(accountId);
            assertTrue(account.isPresent());
            assertEquals("displayName", account.get().getDisplayName());
        }

        @Test
        @DisplayName("should return 403 if user wants to update other user's password")
        public void t403() {
            webTestClient.patch()
                    .uri(ENDPOINT, accountId + 1)
                    .bodyValue(new PatchUserRequest("Pass1234", "New12345"))
                    .header("User-Authorization", "Bearer accessToken")
                    .exchange()
                    .expectStatus().isForbidden();
        }
    }
}
