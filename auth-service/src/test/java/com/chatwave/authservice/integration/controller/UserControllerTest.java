package com.chatwave.authservice.integration.controller;

import com.chatwave.authservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.authservice.domain.dto.CreateUserRequest;
import com.chatwave.authservice.domain.dto.PatchPasswordRequest;
import com.chatwave.authservice.domain.dto.TokenSetResponse;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.integration.utils.ClientAuthUtils;
import com.chatwave.authservice.repository.SessionRepository;
import com.chatwave.authservice.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.rmi.UnexpectedException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Too deeper understanding tests look at {@link  ClientAuthUtils}
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
    @DisplayName("POST /users/")
    class createUser {
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
    class authenticateUser {
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
    @DisplayName("PATCH /users/{userId}/password")
    class patchUserPassword {
        private final String ENDPOINT = "/users/1/password";
        private User user;
        @BeforeEach
        void setUp() {
            user = createAndSaveUser();
        }

        @Test
        @DisplayName("should change user's password")
        public void t200() throws UnexpectedException {
            var patchPasswordRequest = new PatchPasswordRequest("Pass1234", "NewPass1");

            webTestClient.patch()
                    .uri(ENDPOINT)
                    .bodyValue(patchPasswordRequest)
                    .header("Authorization", getAuthHeader())
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
            var patchPasswordRequest = new PatchPasswordRequest("Pass1234", "invalid");

            webTestClient.patch()
                    .uri(ENDPOINT)
                    .bodyValue(patchPasswordRequest)
                    .header("Authorization", getAuthHeader())
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo("BAD_REQUEST");
        }

        @Test
        @DisplayName("should return 401 if no authorities are provided")
        public void t401() {
            var patchPasswordRequest = new PatchPasswordRequest("Pass1234", "NewPass1");

            webTestClient.patch()
                    .uri(ENDPOINT)
                    .bodyValue(patchPasswordRequest)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
    }
}

