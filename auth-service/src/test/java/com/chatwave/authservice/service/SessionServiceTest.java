package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService")
public class SessionServiceTest {
    @InjectMocks
    private SessionServiceImpl service;
    @Mock
    private SessionRepository repository;
    private User user;
    private Session session;

    @BeforeEach
    void setup() {
        // setup User
        user = new User();
        user.setId(1);
        user.setPassword("pass");

        session = new Session(user);
        session.setAccessToken("access");
        session.setRefreshToken("access");
    }

    @Nested
    @DisplayName("createSession( user )")
    class createSession {
        @Test
        @DisplayName("should create and return session")
        public void t1() {
            when(
                repository.save( isA(Session.class) )
            ).thenAnswer(invocation -> {
                Session invSession = invocation.getArgument(0);
                invSession.setId(2L);
                return invSession;
            });

            var session = service.createSession(user);

            verify(repository, times(1))
                    .save( isA(Session.class) );

            assertNotNull(session);
            assertEquals(2L, session.getId());
            assertEquals(user, session.getUser());
        }
    }

    @Nested
    @DisplayName("refreshSession( refreshToken )")
    class refresh {
        @Test
        @DisplayName("should refresh session")
        public void t1() {
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

            assertEquals(user, result.getUser());
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
    @DisplayName("getUserCurrentSessions( userId )")
    class getUserCurrentSessions {
        @Test
        @DisplayName("should return all user's current sessions")
        public void t1() {
            var sessions = List.of(new Session(), new Session());

            when(
                    repository.findAllNotExpiredByUserId(1)
            ).thenReturn(sessions);

            var result = service.getUserCurrentSessions(1);
            assertEquals(sessions, result);
        }
    }

    @Nested
    @DisplayName("expireAllUserSessions( userId )")
    class expireAllUserSessions {
        @Test
        @DisplayName("should expire all unexpired user's session")
        public void t1() {
            var session1 = Mockito.mock(Session.class);
            var session2 = Mockito.mock(Session.class);
            var session3 = Mockito.mock(Session.class);

            when(
                    repository.findAllNotExpiredByUserId(1)
            ).thenReturn(List.of(session1, session2, session3));

            service.expireAllUserSessions(1);

            var inOrder = inOrder(session1, session2, session3, repository);

            inOrder.verify(
                    session1, times(1)
            ).expire();

            inOrder.verify(
                    session2, times(1)
            ).expire();

            inOrder.verify(
                    session3, times(1)
            ).expire();

            inOrder.verify(
                    repository, times(1)
            ).saveAll(List.of(session1, session2, session3));
        }

        @Test
        @DisplayName("should not throw exception if sessionList is empty")
        public void t2() {
            when(
                    repository.findAllNotExpiredByUserId(1)
            ).thenReturn(List.of());

            service.expireAllUserSessions(1);
        }
    }

    @Nested
    @DisplayName("expireUserSession( sessionId, userId )")
    class expireUserSession {
        @Test
        @DisplayName("should expire user's session")
        public void t1() {
            var session = Mockito.mock(Session.class);
            var user = new User();
            user.setId(1);

            when(
                    repository.findById(2L)
            ).thenReturn(Optional.of(session));

            when(
                    session.getUser()
            ).thenReturn(user);

            service.expireUserSession(1, 2L);

            verify(session, times(1))
                    .expire();

            verify(repository, times(1))
                    .save(session);
        }

        @Test
        @DisplayName("should throw NOT_FOUND if token with given ID does not exist")
        public void t2() {
            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> service.expireUserSession(1, 2L)
            );

            assertEquals(NOT_FOUND, thrown.getStatusCode());
        }

        @Test
        @DisplayName("should throw NOT_FOUND if the session is of different user")
        public void t3() {
            var session = Mockito.mock(Session.class);
            var user = new User();
            user.setId(2);

            when(
                    repository.findById(2L)
            ).thenReturn(Optional.of(session));

            when(
                    session.getUser()
            ).thenReturn(user);

            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> service.expireUserSession(1, 2L)
            );

            assertEquals(NOT_FOUND, thrown.getStatusCode());

            verify(repository, times(0))
                    .save(session);
        }

        @Test
        @DisplayName("should throw BAD_REQUEST if the session is already expired")
        public void t4() {
            var session = Mockito.mock(Session.class);
            var user = new User();
            user.setId(1);

            when(
                    repository.findById(2L)
            ).thenReturn(Optional.of(session));

            when(
                    session.getUser()
            ).thenReturn(user);

            when(
                    session.isExpired()
            ).thenReturn(true);

            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> service.expireUserSession(1, 2L)
            );

            assertEquals(BAD_REQUEST, thrown.getStatusCode());

            verify(repository, times(0))
                    .save(session);
        }
    }
}
