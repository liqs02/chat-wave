package com.chatwave.authservice.unit.service;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.repository.SessionRepository;
import com.chatwave.authservice.repository.UserRepository;
import com.chatwave.authservice.service.SessionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.chatwave.authservice.utils.TestVariables.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService")
public class SessionServiceTest {
    @InjectMocks
    private SessionServiceImpl sessionService;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private UserRepository userRepository;
    private User user;
    private Session session;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(USER_ID);

        session = new Session(user);
        session.setAccessToken(ACCESS_TOKEN);
        session.setRefreshToken(REFRESH_TOKEN);
    }

    @Nested
    @DisplayName("createSession(user)")
    class c1 {
        @Test
        @DisplayName("should create and return session")
        public void t1() {
            when(
                    userRepository.findById(USER_ID)
            ).thenReturn(Optional.of(user));

            when(
                sessionRepository.save( isA(Session.class) )
            ).thenAnswer(i -> {
                var session = (Session) i.getArgument(0);
                session.setId(SESSION_ID);
                return session;
            });

            var session = sessionService.createSession(USER_ID);

            verify(sessionRepository, times(1))
                    .save( isA(Session.class) );

            assertNotNull(session);
            assertEquals(SESSION_ID, session.getId());
            assertEquals(user, session.getUser());
        }
    }

    @Nested
    @DisplayName("refreshSession(refreshToken)")
    class c2 {
        @Test
        @DisplayName("should refresh session")
        public void t1() {
            session.setAccessTokenExpireDate(LocalDateTime.now().minusMinutes(1));

            when(
                sessionRepository.findByRefreshToken(REFRESH_TOKEN)
            ).thenReturn( Optional.of(session) );

            var result = sessionService.refreshSession(REFRESH_TOKEN);

            verify(sessionRepository, times(1))
                    .findByRefreshToken(REFRESH_TOKEN);

            verify(sessionRepository, times(1))
                    .save(isA(Session.class));

            assertNotEquals(REFRESH_TOKEN, session.getRefreshToken());
            assertNotEquals("access", session.getAccessToken());

            assertEquals(user, result.getUser());
        }

        @Test
        @DisplayName("should throw BAD_REQUEST if token with given ID does not exist")
        public void t2() {
            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> sessionService.refreshSession("invalid")
            );

            assertEquals(BAD_REQUEST, thrown.getStatusCode());
        }

        @Test
        @DisplayName("should throw BAD_REQUEST if session is expired")
        public void t3() {
            session.setExpireDate(LocalDate.now());
            session.setAccessTokenExpireDate(LocalDateTime.now().minusDays(1));

            when(
                    sessionRepository.findByRefreshToken(REFRESH_TOKEN)
            ).thenReturn( Optional.of(session) );

            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> sessionService.refreshSession(REFRESH_TOKEN)
            );

            assertEquals(BAD_REQUEST, thrown.getStatusCode());
        }

        @Test
        @DisplayName("should throw BAD_REQUEST if session access token is not expired")
        public void t4() {
            session.setExpireDate(LocalDate.now());

            when(
                    sessionRepository.findByRefreshToken(REFRESH_TOKEN)
            ).thenReturn( Optional.of(session) );

            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> sessionService.refreshSession(REFRESH_TOKEN)
            );

            assertEquals(BAD_REQUEST, thrown.getStatusCode());
        }
    }

    @Nested
    @DisplayName("getNotExpiredSessionsByUserId(userId)")
    class c3 {
        @Test
        @DisplayName("should return all user's current sessions")
        public void t1() {
            var sessions = List.of(new Session(), new Session());

            when(
                    sessionRepository.findAllNotExpiredByUserId(USER_ID)
            ).thenReturn(sessions);

            var result = sessionService.getNotExpiredSessionsByUserId(USER_ID);
            assertEquals(sessions, result);
        }
    }

    @Nested
    @DisplayName("expireSessionsByUserId(userId)")
    class c4 {
        @Test
        @DisplayName("should expire all unexpired user's session")
        public void t1() {
            var sessions = List.of(mock(Session.class), mock(Session.class), mock(Session.class));

            when(
                    sessionRepository.findAllNotExpiredByUserId(USER_ID)
            ).thenReturn(sessions);

            sessionService.expireSessionsByUserId(USER_ID);


            verify(
                    sessions.get(0), times(1)
            ).expire();

            verify(
                    sessions.get(1), times(1)
            ).expire();

            verify(
                    sessions.get(2), times(1)
            ).expire();

            verify(
                    sessionRepository, times(1)
            ).saveAll(sessions);
        }

        @Test
        @DisplayName("should not throw exception if sessionList is empty")
        public void t2() {
            when(
                    sessionRepository.findAllNotExpiredByUserId(USER_ID)
            ).thenReturn(List.of());

            sessionService.expireSessionsByUserId(USER_ID);
        }
    }

    @Nested
    @DisplayName("expireSession(sessionId, userId)")
    class c5 {
        @Test
        @DisplayName("should expire user's session")
        public void t1() {
            var session = mock(Session.class);
            var user = new User();
            user.setId(USER_ID);

            when(
                    sessionRepository.findById(SESSION_ID)
            ).thenReturn(Optional.of(session));

            when(
                    session.getUser()
            ).thenReturn(user);

            sessionService.expireSession(SESSION_ID, USER_ID);

            verify(session, times(1))
                    .expire();

            verify(sessionRepository, times(1))
                    .save(session);
        }

        @Test
        @DisplayName("should throw NOT_FOUND if token with given ID does not exist")
        public void t2() {
            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> sessionService.expireSession(SESSION_ID, USER_ID)
            );

            assertEquals(NOT_FOUND, thrown.getStatusCode());
        }

        @Test
        @DisplayName("should throw NOT_FOUND if the session is of different user")
        public void t3() {
            var session = mock(Session.class);
            var user = new User();
            user.setId(2);

            when(
                    sessionRepository.findById(SESSION_ID)
            ).thenReturn(Optional.of(session));

            when(
                    session.getUser()
            ).thenReturn(user);

            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> sessionService.expireSession(SESSION_ID, USER_ID)
            );

            assertEquals(NOT_FOUND, thrown.getStatusCode());

            verify(sessionRepository, times(0))
                    .save(session);
        }

        @Test
        @DisplayName("should throw BAD_REQUEST if the session is already expired")
        public void t4() {
            var session = mock(Session.class);
            var user = new User();
            user.setId(USER_ID);

            when(
                    sessionRepository.findById(SESSION_ID)
            ).thenReturn(Optional.of(session));

            when(
                    session.getUser()
            ).thenReturn(user);

            when(
                    session.isExpired()
            ).thenReturn(true);

            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> sessionService.expireSession(SESSION_ID, USER_ID)
            );

            assertEquals(BAD_REQUEST, thrown.getStatusCode());

            verify(sessionRepository, times(0))
                    .save(session);
        }
    }
}
