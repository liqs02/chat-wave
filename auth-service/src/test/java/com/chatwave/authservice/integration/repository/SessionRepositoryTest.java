package com.chatwave.authservice.integration.repository;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.repository.SessionRepository;
import com.chatwave.authservice.repository.UserRepository;
import com.chatwave.authservice.utils.ContainersConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.chatwave.authservice.utils.TestVariables.*;
import static org.junit.jupiter.api.Assertions.*;

@Import(ContainersConfig.class)
@SpringBootTest
@DisplayName("SessionRepository")
public class SessionRepositoryTest {
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private UserRepository userRepository;
    private Session session;
    private User user;

    @BeforeEach
    void setup() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setLoginName(LOGIN_NAME);
        user.setPassword(PASSWORD);
        userRepository.save(user);

        session = new Session();
        session.setUser(user);
        session.setAccessToken(ACCESS_TOKEN);
        session.setRefreshToken(REFRESH_TOKEN);
        sessionRepository.save(session);
    }

    @AfterEach
    void tearDown() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("findByRefreshToken()")
    public void t1() {
        var exists = sessionRepository.findByRefreshToken(REFRESH_TOKEN);
        if(exists.isEmpty()) fail();

        var foundSession = exists.get();

        assertEquals(session, foundSession);
    }

    @Test
    @DisplayName("findByAccessToken()")
    public void t2() {
        var exists = sessionRepository.findByAccessToken(ACCESS_TOKEN);
        if(exists.isEmpty()) fail();

        var foundSession = exists.get();

        assertEquals(session, foundSession);
    }

    @Test
    @DisplayName("findByAccessToken()")
    public void t3() {
        var exists = sessionRepository.findByAccessToken(ACCESS_TOKEN);
        if(exists.isEmpty()) fail();

        var foundSession = exists.get();

        assertEquals(session, foundSession);
    }

    @Test
    @DisplayName("findAllNotExpiredById()")
    public void t4() {
        var session1 = session;

        var session2 = new Session();
        session2.setUser(user);
        sessionRepository.save(session2);

        var session3 = new Session();
        session3.setUser(user);
        session3.setExpireDate(LocalDate.now());
        sessionRepository.save(session3);

        assertEquals(3, sessionRepository.findAll().size());

        var sessions = sessionRepository.findAllNotExpiredByUserId(user.getId());

        assertEquals(List.of(session1, session2), sessions);
    }

    @Nested
    @DisplayName("findNotExpiredByAccessToken()")
    class c1 {
        @Test
        @DisplayName("should returns valid session")
        public void t1() {
            var optional = sessionRepository.findNotExpiredByAccessToken(ACCESS_TOKEN);
            if(optional.isEmpty()) fail();
            assertEquals(session, optional.get());
        }

        @Test
        @DisplayName("shouldn't return session if accessToken is expired")
        public void t2() {
            session.setAccessTokenExpireDate(LocalDateTime.now());
            sessionRepository.save(session);

            var optional = sessionRepository.findNotExpiredByAccessToken(ACCESS_TOKEN);
            assertTrue(optional.isEmpty());
        }

        @Test
        @DisplayName("shouldn't return session if is expired")
        public void t3() {
            session.setExpireDate(LocalDate.now());
            sessionRepository.save(session);

            var optional = sessionRepository.findNotExpiredByAccessToken(ACCESS_TOKEN);
            assertTrue(optional.isEmpty());
        }
    }

    @Test
    @DisplayName("findNotExpiredByIdAndUserId( sessionId, userId ) should find session")
    public void t5() {
        var found = sessionRepository.findNotExpiredByIdAndUserId(session.getId(),user.getId());
        if(found.isEmpty())
            fail();
        assertEquals(session, found.get());
    }

    @Test
    @DisplayName("findAllExpiredNotCleaned() should find expired sessions that have a accessToken or refreshToken")
    public void t6() {
        // session with refreshToken
        var session1 = new Session(user);
        session1.setAccessToken(null);
        session1.setExpireDate(LocalDate.now());

        // expired session with accessToken
        var session2 = new Session(user);
        session2.setRefreshToken(null);
        session2.setExpireDate(LocalDate.now());

        var session3 = new Session(user);
        session3.setRefreshToken(null);
        session3.setAccessToken(null);
        session3.setExpireDate(LocalDate.now());

        sessionRepository.saveAll(List.of(session1, session2, session3));


        var found = sessionRepository.findAllExpiredNotCleaned();
        assertEquals(
                List.of(session1, session2), found
        );
    }
}