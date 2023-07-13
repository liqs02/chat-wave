package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.Session;
import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.repository.SessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService")
public class SessionServiceTest {
    @InjectMocks
    private SessionServiceImpl service;
    @Mock
    private SessionRepository repository;

    User createUser() {
        var user = new User();
        user.setId(1);
        user.setPassword("pass");

        return user;
    }

    Session createSession() {
        var user = createUser();

        var session = new Session();
        session.setId(1L);
        session.setUser(user);
        session.setRefreshToken("refresh");

        return session;
    }

    @Nested
    @DisplayName("createSession( user )")
    class createSession {
        @Test
        @DisplayName("should create and return session")
        public void t1() {
            var user = createUser();

            when(
                repository.save( isA(Session.class) )
            ).thenAnswer(invocation -> {
                Session invSession = invocation.getArgument(0);
                invSession.setId(1L);
                return invSession;
            });

            var session = service.createSession(user);

            verify(repository, times(1))
                    .save( isA(Session.class) );

            assertNotNull(session);
            assertEquals(1L, session.getId());
            assertEquals(user, session.getUser());
        }
    }

    @Nested
    @DisplayName("getActiveSession( accessToken )")
    class getActiveSession {
        @Test
        @DisplayName("should return active session if access token is valid")
        public void t1() {
            var session = createSession();
            session.setAccessTokenExpireDate(LocalDateTime.now().plusMinutes(1));

            when(
                repository.findByAccessToken("access")
            ).thenReturn( Optional.of(session) );

            var result = service.getActiveSession("access");

            verify(repository, times(1))
                    .findByAccessToken("access");

            assertEquals(session, result.orElse(null));
        }

        @Test
        @DisplayName("should return empty optional if access token is not found")
        public void t2() {
            when(
                repository.findByAccessToken("invalid")
            ).thenReturn( Optional.empty() );

            var result = service.getActiveSession("invalid");

            verify(repository, times(1))
                    .findByAccessToken("invalid");

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should return empty optional if access token is expired")
        public void t3() {
            var session = createSession();
            session.setAccessTokenExpireDate(LocalDateTime.now().minusMinutes(1));

            when(
                repository.findByAccessToken("access")
            ).thenReturn( Optional.of(session) );

            var result = service.getActiveSession("access");

            verify(repository, times(1))
                    .findByAccessToken("access");

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("refreshSession( refreshToken )")
    class refresh {
        @Test
        @DisplayName("should refresh session")
        public void t1() throws NoSuchFieldException, IllegalAccessException {
            var session = createSession();
            session.setAccessTokenExpireDate(LocalDateTime.now().minusMinutes(1));

            when(
                repository.findByRefreshToken("refresh")
            ).thenReturn( Optional.of(session) );

            var result = service.refreshSession("refresh");

            verify(repository, times(1))
                    .findByRefreshToken("refresh");

            verify(repository, times(1))
                    .save(isA(Session.class));

            assertNotEquals("refresh", session.getRefreshToken());
            assertNotEquals("access", session.getAccessToken());

            assertEquals(createUser(), result.getUser());
        }

        @Test
        @DisplayName("should throw BAD_REQUEST if token with given ID does not exist")
        public void t2() {
            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> service.refreshSession("invalid")
            );

            assertEquals(BAD_REQUEST, thrown.getStatusCode());
        }

        @Test
        @DisplayName("should throw BAD_REQUEST if session is expired")
        public void t3() {
            var session = createSession();
            session.setExpireDate(LocalDate.now());
            session.setAccessTokenExpireDate(LocalDateTime.now().minusDays(1));

            when(
                    repository.findByRefreshToken("refresh")
            ).thenReturn( Optional.of(session) );

            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> service.refreshSession("refresh")
            );

            assertEquals(BAD_REQUEST, thrown.getStatusCode());
        }

        @Test
        @DisplayName("should throw BAD_REQUEST if session access token is not expired")
        public void t4() {
            var session = createSession();
            session.setExpireDate(LocalDate.now());

            when(
                    repository.findByRefreshToken("refresh")
            ).thenReturn( Optional.of(session) );

            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> service.refreshSession("refresh")
            );

            assertEquals(BAD_REQUEST, thrown.getStatusCode());
        }
    }

    @Nested
    @DisplayName("deactivateSession( sessionId )")
    class invalidate {
        @Test
        @DisplayName("should deactivate session")
        public void t1() {
            var session = Mockito.mock(Session.class);

            when(
                    repository.findById(1L)
            ).thenReturn(Optional.of(session));

            service.deactivateSession(1L);

            verify(session, times(1))
                    .deactivate();

            verify(repository, times(1))
                    .save(session);
        }

        @Test
        @DisplayName("should throw BAD_REQUEST if token with given ID does not exist")
        public void t2() {
            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> service.deactivateSession(1L)
            );

            assertEquals(BAD_REQUEST, thrown.getStatusCode());
        }
    }

}
