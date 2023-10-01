package com.chatwave.authservice.integration.scheduler;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.repository.SessionRepository;
import com.chatwave.authservice.repository.UserRepository;
import com.chatwave.authservice.scheduler.SessionScheduler;
import com.chatwave.authservice.utils.ContainersConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.chatwave.authservice.utils.TestVariables.LOGIN_NAME;
import static com.chatwave.authservice.utils.TestVariables.PASSWORD;
import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

/**
 * It does not test a @Scheduled annotation.
 */
@Import(ContainersConfig.class)
@SpringBootTest
public class SessionSchedulerTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private SessionScheduler sessionScheduler;
    private Long expiredSessionId;


    @BeforeEach
    void setUp() {
        var user = new User();
        user.setLoginName(LOGIN_NAME);
        user.setPassword(PASSWORD);
        userRepository.save(user);

        // expired not cleaned session
        var session1 = new Session(user);
        session1.setExpireDate(LocalDate.now().minusDays(1));

        // not expired session with expired accessToken
        var session2 = new Session(user);
        session2.setAccessTokenExpireDate(LocalDateTime.now().minusSeconds(1));

        sessionRepository.saveAll(
                List.of(session1, session2)
        );

        expiredSessionId = session1.getId();
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

        var session1 = sessions.get(0);
        var session2 = sessions.get(1);

        if(session1.getId().equals(expiredSessionId)) {
            assertNull(session1.getAccessToken());
            assertNull(session1.getRefreshToken());

            assertNotNull(session2.getAccessToken());
            assertNotNull(session2.getRefreshToken());
        } else {
            assertNotNull(session1.getAccessToken());
            assertNotNull(session1.getRefreshToken());

            assertNull(session2.getAccessToken());
            assertNull(session2.getRefreshToken());
        }

    }
}

