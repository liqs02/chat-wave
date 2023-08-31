package com.chatwave.authservice.integration.controller;

import com.chatwave.authservice.domain.dto.RefreshSessionRequest;
import com.chatwave.authservice.domain.dto.SessionResponse;
import com.chatwave.authservice.domain.dto.TokenSetResponse;
import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.integration.utils.UserAuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

/**
 * Too deeper understanding tests look at {@link  UserAuthUtils}
 */
@DisplayName("SessionController integration tests")
public class SessionControllerTest extends UserAuthUtils {
    @Nested
    @DisplayName("GET /users/{userId}/sessions")
    class getActiveSessionsByUserId {
        private Session secondSession;

        @BeforeEach
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        void setUp() {
            secondSession = new Session(userRepository.findById(1).get());
            sessionRepository.save(secondSession);
        }

        @Test
        @DisplayName("should return all user's sessions")
        public void t200() {
            String ENDPOINT = "/users/1/sessions";
            var sessions = webTestClient
                    .get()
                    .uri(ENDPOINT)
                    .header("User-Authorization", getAuthHeader())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(SessionResponse.class)
                    .returnResult().getResponseBody();

            assertNotNull(sessions);
            assertEquals(2, sessions.size());

            var foundSession = sessions.get(0);
            assertEquals(session.getId(), foundSession.id());
            assertEquals(session.getExpireDate(), foundSession.expireDate());
            assertEquals(session.getAccessTokenExpireDate(), foundSession.accessTokenExpireDate());
            assertEquals(session.getCreatedAt(), foundSession.createdAt());

            foundSession = sessions.get(1);
            assertEquals(secondSession.getId(), foundSession.id());
            assertEquals(secondSession.getExpireDate(), foundSession.expireDate());
            assertEquals(secondSession.getAccessTokenExpireDate(), foundSession.accessTokenExpireDate());
            assertEquals(secondSession.getCreatedAt(), foundSession.createdAt());
        }
    }

    @Nested
    @DisplayName("POST /users/sessions/refresh")
    class refreshTokens {

        @BeforeEach
        void setUp() {
            session.setAccessTokenExpireDate(LocalDateTime.now().minusSeconds(1));
            sessionRepository.save(session);
        }

        @Test
        @DisplayName("should return all user's sessions")
        public void t200() {
            String ENDPOINT = "/users/sessions/refresh";
            var tokenSet = webTestClient
                    .mutateWith(csrf())
                    .post()
                    .uri(ENDPOINT)
                    .bodyValue(new RefreshSessionRequest(session.getRefreshToken()))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(TokenSetResponse.class)
                    .returnResult().getResponseBody();

            assertNotNull(tokenSet);
            assertNotNull(tokenSet.accessToken());
            assertNotNull(tokenSet.refreshToken());

            assertNotEquals(session.getAccessToken(), tokenSet.accessToken());
            assertNotEquals(session.getRefreshToken(), tokenSet.refreshToken());
        }
    }

    @Nested
    @DisplayName("DELETE /users/{userId}/sessions")
    class expireUserSessions {


        @BeforeEach
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        void setUp() {
            Session secondSession = new Session(userRepository.findById(1).get());
            sessionRepository.save(secondSession);
        }

        @Test
        @DisplayName("should expire all active user's sessions")
        public void t200() {
            String ENDPOINT = "/users/1/sessions";
            webTestClient
                    .mutateWith(csrf()) // todo: fix this, it throws NPE (tests are correct)
                    .delete()
                    .uri(ENDPOINT)
                    .header("User-Authorization", getAuthHeader())
                    .exchange()
                    .expectStatus().isOk();

            var optional1 = sessionRepository.findById(session.getId());
            if(optional1.isEmpty())
                fail();
            assertTrue(optional1.get().isExpired());

            var optional2 = sessionRepository.findById(session.getId());
            if(optional2.isEmpty())
                fail();
            assertTrue(optional2.get().isExpired());
        }
    }

    @Nested
    @DisplayName("DELETE /users/{userId}/sessions/{sessionId}")
    class expireSession {
        private String getEndpoint() {
            return "/users/1/sessions/" + session.getId();
        }

        @Test
        @DisplayName("should expire user's session")
        public void t200() {
            webTestClient
                    .mutateWith(csrf())
                    .delete()
                    .uri(getEndpoint())
                    .header("User-Authorization", getAuthHeader())
                    .exchange()
                    .expectStatus().isOk();

            var optionalSession = sessionRepository.findById(session.getId());
            if(optionalSession.isEmpty())
                fail();
            assertTrue(optionalSession.get().isExpired());
        }

    }

}