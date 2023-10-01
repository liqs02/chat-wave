package com.chatwave.authservice.integration.controller;

import com.chatwave.authservice.domain.dto.request.AuthenticationRequest;
import com.chatwave.authservice.domain.dto.request.RegisterRequest;
import com.chatwave.authservice.domain.dto.request.UpdatePasswordRequest;
import com.chatwave.authservice.domain.dto.response.AuthenticationResponse;
import com.chatwave.authservice.domain.dto.response.RegisterResponse;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.repository.UserRepository;
import com.chatwave.authservice.utils.ContainersConfig;
import com.chatwave.authservice.utils.OAuthClientService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.chatwave.authservice.utils.TestVariables.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Import(ContainersConfig.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("UserController integration tests")
public class UserControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private OAuthClientService oauthClientService;
    private Integer userId;

    private User createAndSaveUser() {
        var encoded = passwordEncoder.encode(PASSWORD);

        var user = new User();
        user.setLoginName(LOGIN_NAME);
        user.setPassword(encoded);

        userRepository.save(user);
        userId = user.getId();

        return user;
    }

    @BeforeEach
    void setUp() {
        if(oauthClientService == null)
            oauthClientService = new OAuthClientService(webTestClient);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /users")
    class c2 {
        private final String ENDPOINT = "/users";

        @Test
        @DisplayName("should create user and return id")
        public void t200() {
            var createUserRequest = new RegisterRequest(LOGIN_NAME, PASSWORD);

            var createUserResponse = webTestClient.post()
                    .uri(ENDPOINT)
                    .bodyValue(createUserRequest)
                    .header("Authorization", oauthClientService.getAuthHeader())
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(RegisterResponse.class)
                    .returnResult().getResponseBody();

            assertNotNull(createUserResponse);
            assertNotNull(createUserResponse.userId());

            if(userRepository.findById(createUserResponse.userId()).isEmpty())
                fail("User was not saved.");
        }

        @Test
        @DisplayName("should throw BAD_REQUEST if data is invalid (validation test)")
        public void t400() {
        var createUserRequest = new RegisterRequest(LOGIN_NAME, INVALID_PASSWORD);

        webTestClient.post()
                .uri(ENDPOINT)
                .bodyValue(createUserRequest)
                .header("Authorization", oauthClientService.getAuthHeader())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo("BAD_REQUEST");
        }

        @Test
        @DisplayName("should return 401 if no authorities are provided")
        public void t401() {
            var createUserRequest = new RegisterRequest(LOGIN_NAME, PASSWORD);
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
        @DisplayName("should return OK status")
        public void t200() {
           var result = webTestClient.post()
                    .uri(ENDPOINT)
                    .bodyValue(new AuthenticationRequest(LOGIN_NAME, PASSWORD))
                    .header("Authorization", oauthClientService.getAuthHeader())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(AuthenticationResponse.class)
                    .returnResult().getResponseBody();

            assertNotNull(result);
            assertEquals(userId, result.userId());
        }

        @Test
        @DisplayName("should throw BAD_REQUEST if data is invalid (validation test)")
        public void t400() {
            webTestClient.post()
                    .uri(ENDPOINT)
                    .bodyValue(new AuthenticationRequest("", PASSWORD))
                    .header("Authorization", oauthClientService.getAuthHeader())
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo("BAD_REQUEST");
        }

        @Test
        @DisplayName("should return 401 if no authorities are provided")
        public void t401() {
            var authenticateUserRequest = new AuthenticationRequest(null, PASSWORD);

            webTestClient.post()
                    .uri(ENDPOINT)
                    .bodyValue(authenticateUserRequest)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
    }

    @Nested
    @DisplayName("PUT /users/{userId}/password")
    class c4 {
        private final String ENDPOINT = "/users/{userId}/password";
        private User user;
        @BeforeEach
        void setUp() {
            user = createAndSaveUser();
        }

        @Test
        @DisplayName("should change user's password")
        public void t200() {
            webTestClient.put()
                    .uri(ENDPOINT, userId)
                    .header("Authorization", oauthClientService.getAuthHeader())
                    .bodyValue(new UpdatePasswordRequest(PASSWORD_2))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody().isEmpty();

            var optionalUser = userRepository.findById(userId);

            if(optionalUser.isEmpty())
                fail("User was not saved.");

            assertNotEquals(user.getPassword(), optionalUser.get().getPassword());
        }

        @Test
        @DisplayName("should throw BAD_REQUEST if data is invalid (validation test)")
        public void t400() {
            webTestClient.put()
                    .uri(ENDPOINT, userId)
                    .header("Authorization", oauthClientService.getAuthHeader())
                    .bodyValue(new UpdatePasswordRequest(INVALID_PASSWORD))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo("BAD_REQUEST");
        }

        @Test
        @DisplayName("should return 401 if no authorities are provided")
        public void t401() {
            webTestClient.put()
                    .uri(ENDPOINT, userId)
                    .bodyValue(new UpdatePasswordRequest(PASSWORD_2))
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
    }
}

