package com.chatwave.authservice.integration.scheduler;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.repository.SessionRepository;
import com.chatwave.authservice.repository.UserRepository;
import com.chatwave.authservice.scheduler.SessionScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.chatwave.authservice.utils.TestVariables.LOGIN_NAME;
import static com.chatwave.authservice.utils.TestVariables.PASSWORD;
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

    @BeforeEach
    void setUp() {
        var user = new User();
        user.setLoginName(LOGIN_NAME);
        user.setPassword(PASSWORD);
        userRepository.save(user);

        var sessions = new ArrayList<Session>();

        // expired not cleaned session
        var session1 = new Session(user);
        session1.setExpireDate(LocalDate.now());

        // not expired session with expired accessToken
        var session2 = new Session(user);
        session2.setAccessTokenExpireDate(LocalDateTime.now());

        sessionRepository.saveAll(
                List.of(session1, session2)
        );
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
