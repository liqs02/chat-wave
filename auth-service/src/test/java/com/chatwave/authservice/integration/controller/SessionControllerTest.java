package com.chatwave.authservice.integration.controller;

import com.chatwave.authclient.domain.UserAuthentication;
import com.chatwave.authservice.domain.dto.request.RefreshSessionRequest;
import com.chatwave.authservice.domain.dto.response.GetSessionResponse;
import com.chatwave.authservice.domain.dto.response.TokenSetResponse;
import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.repository.SessionRepository;
import com.chatwave.authservice.repository.UserRepository;
import com.chatwave.authservice.utils.ContainersConfig;
import com.chatwave.authservice.utils.OAuthClientService;
import com.chatwave.authservice.utils.OAuthUserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Too deeper understanding tests look at {@link  OAuthUserService}
 */
@Import(ContainersConfig.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("SessionController integration tests")
public class SessionControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private OAuthUserService oauthUserService;
    private OAuthClientService oauthClientService;
    private Session session1;

    @BeforeEach
    void setUp() {
        if(oauthClientService == null)
            oauthClientService = new OAuthClientService(webTestClient);
        if(oauthUserService == null)
            oauthUserService = new OAuthUserService(userRepository, sessionRepository, passwordEncoder);

        oauthUserService.createUserAndSession();
        session1 = oauthUserService.getSession();
    }

    @AfterEach
    void tearDown() {
        oauthUserService.cleanDatabase();
    }

    @Nested
    @DisplayName("GET /sessions")
    class c1 {
        private Session session2;

        @BeforeEach
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        void setUp() {
            session2 = new Session(userRepository.findById(oauthUserService.getUserId()).get());
            sessionRepository.save(session2);
        }

        @AfterEach
        void tearDown() {
            sessionRepository.deleteAll();
            userRepository.deleteAll();
        }

        @Test
        @DisplayName("should return all user's sessions")
        public void t200() {
            var sessions = webTestClient.get()
                    .uri("/sessions")
                    .header("User-Authorization", oauthUserService.getAuthHeader())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(GetSessionResponse.class)
                    .returnResult().getResponseBody();

            assertNotNull(sessions);
            assertEquals(2, sessions.size());

            var found1 = sessions.get(0);
            assertEquals(session1.getId(), found1.id());
            assertEquals(session1.getExpireDate(), found1.expireDate());
            assertEquals(session1.getAccessTokenExpireDate(), found1.accessTokenExpireDate());
            assertEquals(session1.getCreatedAt(), found1.createdAt());

            var found2 = sessions.get(1);
            assertEquals(session2.getId(), found2.id());
            assertEquals(session2.getExpireDate(), found2.expireDate());
            assertEquals(session2.getAccessTokenExpireDate(), found2.accessTokenExpireDate());
            assertEquals(session2.getCreatedAt(), found2.createdAt());
        }
    }

    @Nested
    @DisplayName("GET /sessions/authentication")
    class c2 {
        @Test
        @DisplayName("should create userAuthentication and return it")
        public void t200() {
            var userAuthentication = webTestClient.get()
                    .uri("/sessions/authentication")
                    .header("Authorization", oauthClientService.getAuthHeader())
                    .header("User-Authorization", oauthUserService.getAuthHeader())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(UserAuthentication.class)
                    .returnResult().getResponseBody();

            assertNotNull(userAuthentication);
            assertEquals(oauthUserService.getUserId(), userAuthentication.getPrincipal());
            assertEquals(session1.getAccessToken(), userAuthentication.getCredentials());
            assertEquals(session1.getId(), userAuthentication.getDetails().getSessionId());
        }
    }

    @Nested
    @DisplayName("POST /sessions/refresh")
    class c3 {
        @BeforeEach
        void setUp() {
            session1.setAccessTokenExpireDate(LocalDateTime.now().minusSeconds(1));
            sessionRepository.save(session1);
        }

        @Test
        @DisplayName("should return all user's sessions")
        public void t200() {
            var tokenSet = webTestClient.post()
                    .uri("/sessions/refresh")
                    .bodyValue(new RefreshSessionRequest(session1.getRefreshToken()))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(TokenSetResponse.class)
                    .returnResult().getResponseBody();

            assertNotNull(tokenSet);
            assertNotNull(tokenSet.accessToken());
            assertNotNull(tokenSet.refreshToken());

            assertNotEquals(session1.getAccessToken(), tokenSet.accessToken());
            assertNotEquals(session1.getRefreshToken(), tokenSet.refreshToken());
        }
    }

    @Nested
    @DisplayName("DELETE /sessions")
    class c4 {
        @BeforeEach
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        void setUp() {
            var secondSession = new Session(userRepository.findById(oauthUserService.getUserId()).get());
            sessionRepository.save(secondSession);
        }

        @Test
        @DisplayName("should expire all active user's sessions")
        public void t200() {
            webTestClient.delete()
                    .uri("/sessions")
                    .header("User-Authorization", oauthUserService.getAuthHeader())
                    .exchange()
                    .expectStatus().isOk();

            var optional1 = sessionRepository.findById(session1.getId());
            if(optional1.isEmpty())
                fail();
            assertTrue(optional1.get().isExpired());

            var optional2 = sessionRepository.findById(session1.getId());
            if(optional2.isEmpty())
                fail();
            assertTrue(optional2.get().isExpired());
        }
    }

    @Nested
    @DisplayName("DELETE /sessions/{sessionId}")
    class c5 {
        @Test
        @DisplayName("should expire user's session")
        public void t200() {
            webTestClient.delete()
                    .uri("/sessions/{id}", session1.getId())
                    .header("User-Authorization", oauthUserService.getAuthHeader())
                    .exchange()
                    .expectStatus().isOk();

            var optionalSession = sessionRepository.findById(session1.getId());
            if(optionalSession.isEmpty())
                fail();
            assertTrue(optionalSession.get().isExpired());
        }

        @Test
        @DisplayName("should return NOT_FOUND if user wants to expire other user's session")
        public void t404() {
            webTestClient.delete()
                    .uri("/sessions/{id}", session1.getId() + 1)
                    .header("User-Authorization", oauthUserService.getAuthHeader())
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

}