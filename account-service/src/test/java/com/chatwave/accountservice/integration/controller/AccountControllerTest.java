package com.chatwave.accountservice.integration.controller;

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
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.chatwave.accountservice.utils.JsonUtils.toJson;
import static com.chatwave.accountservice.utils.TestVariables.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("AccountController integration tests")
public class AccountControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private AccountRepository accountRepository;
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

    @AfterEach
    public void tearDown() {
        accountRepository.deleteAll();
    }

    private void createAndSaveAccount() {
        var account = new Account();
        account.setId(USER_ID);
        account.setDisplayName(DISPLAY_NAME);

        accountRepository.save(account);

        var userAuthentication = new UserAuthentication();
        userAuthentication.setUserId(account.getId());
        userAuthentication.setDetails(new UserAuthenticationDetails());

        wireMockServer.stubFor(
                get("/sessions/authentication")
                        .withHeader("User-Authorization", equalTo(BEARER_TOKEN))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", APPLICATION_JSON)
                                        .withBody(toJson(userAuthentication)))
        );
    }

    @Nested
    @DisplayName("POST /accounts")
    public class c1 {
        @Test
        @DisplayName("should create an account")
        public void t1() {
            wireMockServer.stubFor(
                    post("/users")
                            .withRequestBody(equalToJson(toJson(REGISTER_REQUEST)))
                            .willReturn(
                                    aResponse()
                                            .withHeader("Content-Type", APPLICATION_JSON)
                                            .withBody(toJson(new RegisterResponse(USER_ID))))
            );

            wireMockServer.stubFor(
                    post("/sessions")
                            .withRequestBody(equalToJson(toJson(CREATE_SESSION_REQUEST)))
                            .willReturn(
                                    aResponse()
                                            .withHeader("Content-Type", APPLICATION_JSON)
                                            .withBody(toJson(TOKEN_SET)))
            );

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
            wireMockServer.stubFor(
                    post("/users/authenticate")
                            .withRequestBody(equalToJson(toJson(AUTHENTICATION_REQUEST)))
                            .willReturn(
                                    aResponse()
                                            .withHeader("Content-Type", APPLICATION_JSON)
                                            .withBody(toJson(new AuthenticationResponse(USER_ID))))
            );

            wireMockServer.stubFor(
                    post("/sessions")
                            .withRequestBody(equalToJson(toJson(CREATE_SESSION_REQUEST)))
                            .willReturn(
                                    aResponse()
                                            .withHeader("Content-Type", APPLICATION_JSON)
                                            .withBody(toJson(TOKEN_SET)))
            );

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
            createAndSaveAccount();

            var result = webTestClient.get()
                    .uri("/accounts/{accountId}/showcase", USER_ID)
                    .header("User-Authorization", BEARER_TOKEN)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(AccountResponse.class)
                    .returnResult().getResponseBody();

            assertNotNull(result);
            assertEquals(USER_ID, result.id());
            assertEquals(DISPLAY_NAME, result.displayName());
        }
    }

    @Nested
    @DisplayName("PATCH /accounts/{accountId}")
    public class c4 {
        private final String ENDPOINT = "/accounts/{accountId}";
        @BeforeEach
        void setUp() {
            createAndSaveAccount();
        }

        @Test
        @DisplayName("should update user's displayName and password")
        public void t200() { // todo: verify that PUT /users/1/password is invoked
            wireMockServer.stubFor(
                    put("/users/1/password")
                            .withRequestBody(equalToJson(toJson(PATCH_USER_REQUEST)))
                            .willReturn( aResponse().withStatus(200) )
            );

            webTestClient.patch()
                    .uri(ENDPOINT, USER_ID)
                    .bodyValue(new PatchAccountRequest(DISPLAY_NAME_2, PASSWORD_2))
                    .header("User-Authorization", BEARER_TOKEN)
                    .exchange()
                    .expectStatus().isOk();

            var account = accountRepository.findById(USER_ID);
            assertTrue(account.isPresent());
            assertEquals(DISPLAY_NAME_2, account.get().getDisplayName());
        }

        @Test
        @DisplayName("should do not update displayName if feignClient will throw exception")
        public void t400() {
            wireMockServer.stubFor(
                       WireMock.put("/users/1/password")
                            .willReturn( aResponse().withStatus(400) )
            );

            webTestClient.patch()
                    .uri(ENDPOINT, USER_ID)
                    .bodyValue(new PatchAccountRequest(DISPLAY_NAME_2, PASSWORD_2))
                    .header("User-Authorization", BEARER_TOKEN)
                    .exchange()
                    .expectStatus().isBadRequest();

            var account = accountRepository.findById(USER_ID);
            assertTrue(account.isPresent());
            assertEquals(DISPLAY_NAME, account.get().getDisplayName());
        }

        @Test
        @DisplayName("should return 403 if user wants to update other user's password")
        public void t403() {
            webTestClient.patch()
                    .uri(ENDPOINT, USER_ID + 1)
                    .bodyValue(new PatchAccountRequest(PASSWORD, PASSWORD_2))
                    .header("User-Authorization", BEARER_TOKEN)
                    .exchange()
                    .expectStatus().isForbidden();
        }
    }
}
