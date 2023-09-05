package com.chatwave.authservice.integration.controller;

import com.chatwave.authclient.domain.UserAuthentication;
import com.chatwave.authservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.authservice.domain.dto.CreateUserRequest;
import com.chatwave.authservice.domain.dto.PatchUserRequest;
import com.chatwave.authservice.domain.dto.TokenSetResponse;
import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.integration.utils.ClientAuthUtils;
import com.chatwave.authservice.repository.SessionRepository;
import com.chatwave.authservice.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.rmi.UnexpectedException;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Too deeper understanding tests look at {@link  ClientAuthUtils}
 * Notice that UserAuthentication domain is taken from auth-client library to check that object will be correctly form.
 */
@DisplayName("UserController integration tests")
public class UserControllerTest extends ClientAuthUtils {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User createAndSaveUser() {
        var encoded = passwordEncoder.encode("Pass1234");

        var user = new User();
        user.setId(1);
        user.setPassword(encoded);
        userRepository.save(user);

        return user;
    }

    @AfterEach
    public void tearDown() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /users/authentication")
    class c1 {
        @Test
        @DisplayName("should create userAuthentication and return it")
        public void t200() throws UnexpectedException {
            var user= createAndSaveUser();
            var session = new Session(user);
            sessionRepository.save(session);

            var userAuthentication = webTestClient.get()
                    .uri("/users/authentication")
                    .header("Authorization", getAuthHeader())
                    .header("User-Authorization", "Bearer " + session.getAccessToken())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(UserAuthentication.class)
                    .returnResult().getResponseBody();

            assertNotNull(userAuthentication);
            assertEquals(user.getId(), userAuthentication.getPrincipal());
            assertEquals(session.getAccessToken(), userAuthentication.getCredentials());
            assertEquals("1", userAuthentication.getName());
            assertEquals(session.getId(), userAuthentication.getDetails().getSessionId());
        }
    }

    @Nested
    @DisplayName("POST /users")
    class c2 {
        private final String ENDPOINT = "/users";

        @Test
        @DisplayName("should create user and return tokens")
        public void t200() throws UnexpectedException {
            var createUserRequest = new CreateUserRequest(1, "Pass1234");

            var tokenSet = webTestClient.post()
                    .uri(ENDPOINT)
                    .bodyValue(createUserRequest)
                    .header("Authorization", getAuthHeader())
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(TokenSetResponse.class)
                    .returnResult().getResponseBody();

            assertNotNull(tokenSet);
            assertNotNull(tokenSet.accessToken());
            assertNotNull(tokenSet.refreshToken());

            if(userRepository.findById(1).isEmpty())
                fail("User was not saved.");

            if(sessionRepository.findByAccessToken(tokenSet.accessToken()).isEmpty() || sessionRepository.findByRefreshToken(tokenSet.refreshToken()).isEmpty())
                fail("Session was not saved.");
        }

        @Test
        @DisplayName("should throw BAD_REQUEST if data is invalid (validation test)")
        public void t400() throws UnexpectedException {
        var createUserRequest = new CreateUserRequest(1, "invalid");

        webTestClient.post()
                .uri(ENDPOINT)
                .bodyValue(createUserRequest)
                .header("Authorization", getAuthHeader())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo("BAD_REQUEST");
        }

        @Test
        @DisplayName("should return 401 if no authorities are provided")
        public void t401() {
            var createUserRequest = new CreateUserRequest(1, "Pass1234");
            webTestClient.post()
                    .uri(ENDPOINT)
                    .bodyValue(createUserRequest)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
    }

    @Nested
    @DisplayName("POST /users/authenticate")
    class c3 {
        private final String ENDPOINT = "/users/authenticate";
        @BeforeEach
        void setUp() {
            createAndSaveUser();
        }

        @Test
        @DisplayName("should return tokens")
        public void t200() throws UnexpectedException {
            var authenticateUserRequest = new AuthenticateUserRequest(1, "Pass1234");

            var tokenSet = webTestClient.post()
                    .uri(ENDPOINT)
                    .bodyValue(authenticateUserRequest)
                    .header("Authorization", getAuthHeader())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(TokenSetResponse.class)
                    .returnResult().getResponseBody();

            assertNotNull(tokenSet);
            assertNotNull(tokenSet.accessToken());
            assertNotNull(tokenSet.refreshToken());

            if(userRepository.findById(1).isEmpty())
                fail("User was not saved.");

            if(sessionRepository.findByAccessToken(tokenSet.accessToken()).isEmpty() || sessionRepository.findByRefreshToken(tokenSet.refreshToken()).isEmpty())
                fail("Session was not saved.");
        }

        @Test
        @DisplayName("should throw BAD_REQUEST if data is invalid (validation test)")
        public void t400() throws UnexpectedException {
            var authenticateUserRequest = new AuthenticateUserRequest(null, "Pass1234");

            webTestClient.post()
                    .uri(ENDPOINT)
                    .bodyValue(authenticateUserRequest)
                    .header("Authorization", getAuthHeader())
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo("BAD_REQUEST");
        }

        @Test
        @DisplayName("should return 401 if no authorities are provided")
        public void t401() {
            var authenticateUserRequest = new AuthenticateUserRequest(null, "Pass1234");

            webTestClient.post()
                    .uri(ENDPOINT)
                    .bodyValue(authenticateUserRequest)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
    }

    @Nested
    @DisplayName("PATCH /users/{userId}")
    class c4 {
        private final String ENDPOINT = "/users/1";
        private User user;
        @BeforeEach
        void setUp() {
            user = createAndSaveUser();
        }

        @Test
        @DisplayName("should change user's password")
        public void t200() throws UnexpectedException {
            var patchUserRequest = new PatchUserRequest("Pass1234", "NewPass1");

            webTestClient.patch()
                    .uri(ENDPOINT)
                    .header("Authorization", getAuthHeader())
                    .bodyValue(patchUserRequest)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody().isEmpty();

            var optionalUser = userRepository.findById(1);

            if(optionalUser.isEmpty())
                fail("User was not saved.");

            assertNotEquals(user.getPassword(), optionalUser.get().getPassword());
        }

        @Test
        @DisplayName("should throw BAD_REQUEST if data is invalid (validation test)")
        public void t400() throws UnexpectedException {
            var patchUserRequest = new PatchUserRequest("Pass1234", "invalid");

            webTestClient.patch()
                    .uri(ENDPOINT)
                    .header("Authorization", getAuthHeader())
                    .bodyValue(patchUserRequest)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo("BAD_REQUEST");
        }

        @Test
        @DisplayName("should return 401 if no authorities are provided")
        public void t401() {
            var patchUserRequest = new PatchUserRequest("Pass1234", "NewPass1");

            webTestClient.patch()
                    .uri(ENDPOINT)
                    .bodyValue(patchUserRequest)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
    }
}

