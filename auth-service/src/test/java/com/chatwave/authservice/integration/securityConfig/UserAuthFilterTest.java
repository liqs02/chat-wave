package com.chatwave.authservice.integration.securityConfig;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.repository.SessionRepository;
import com.chatwave.authservice.repository.UserRepository;
import com.chatwave.authservice.utils.ContainersConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.chatwave.authservice.utils.TestVariables.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Import(ContainersConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("UserAuthFilter integration tests")
public class UserAuthFilterTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;
    private final String ENDPOINT = "/sessions";

    @BeforeEach
    void setUp() {
        var user = new User();
        user.setLoginName(LOGIN_NAME);
        user.setPassword(PASSWORD);
        userRepository.save(user);

        var session = new Session(user);
        session.setAccessToken(ACCESS_TOKEN);
        sessionRepository.save(session);
    }

    @AfterEach
    void tearDown() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("should return OK if user send correct authorization data")
    public void t1() {
        webTestClient.get()
                .uri(ENDPOINT)
                .header("User-Authorization", "Bearer " + ACCESS_TOKEN)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("should return UNAUTHORIZED if user send header without prefix")
    public void t4() {
        webTestClient.get()
                .uri(ENDPOINT)
                .header("User-Authorization", ACCESS_TOKEN)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("should return UNAUTHORIZED if user send empty header")
    public void t5() {
        webTestClient.get()
                .uri(ENDPOINT)
                .header("User-Authorization", "")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("should return UNAUTHORIZED if user didn't send header")
    public void t6() {
        webTestClient.get()
                .uri(ENDPOINT)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
