package com.chatwave.authservice.integration.scheduler;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.repository.SessionRepository;
import com.chatwave.authservice.repository.UserRepository;
import com.chatwave.authservice.scheduler.SessionScheduler;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * It does not test a @Scheduled annotation.
 */
@SpringBootTest
public class SessionSchedulerTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private SessionScheduler sessionScheduler;
    private List<Session> sessions;

    @BeforeEach
    void setUp() {
        var user = new User();
        user.setId(1);
        user.setPassword("Pass1234");
        userRepository.save(user);

        sessions = new ArrayList<Session>();

        // expired not cleaned session
        var session = new Session(user);
        session.setExpireDate(LocalDate.now());
        sessions.add(session);

        // not expired session with expired accessToken
        session = new Session(user);
        session.setAccessTokenExpireDate(LocalDateTime.now());
        sessions.add(session);

        sessionRepository.saveAll(sessions);
    }

    @AfterEach
    void tearDown() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("should clean accessToken and refreshToken of expired sessions")
    public void t1() {
        sessionScheduler.cleanupExpiredSessions();
        var sessions = sessionRepository.findAll();
        assertEquals(2, sessions.size());

        assertNull(sessions.get(0).getAccessToken());
        assertNull(sessions.get(0).getRefreshToken());

        assertNotNull(sessions.get(1).getAccessToken());
        assertNotNull(sessions.get(1).getRefreshToken());
    }
}
