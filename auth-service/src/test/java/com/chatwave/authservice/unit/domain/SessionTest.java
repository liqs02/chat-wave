package com.chatwave.authservice.unit.domain;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.chatwave.authservice.utils.TestVariables.ACCESS_TOKEN;
import static com.chatwave.authservice.utils.TestVariables.REFRESH_TOKEN;
import static org.junit.jupiter.api.Assertions.*;

public class SessionTest {
    private Session session;

    @BeforeEach
    public void setUp() {
        User user = new User();
        session = new Session(user);
    }

    @Nested
    @DisplayName("isExpired()")
    class c1 {
        @Test
        @DisplayName("should return false if session is not expired")
        public void t1() {
            assertFalse(session.isExpired());
        }

        @Test
        @DisplayName("should return true if session is expired")
        public void t2() {
            session.setExpireDate(LocalDate.now());
            assertTrue(session.isExpired());
        }
    }

    @Nested
    @DisplayName("isAccessTokenExpired()")
    class c2 {
        @Test
        @DisplayName("should return false if accessToken is not expired")
        public void t1() {
            assertFalse(session.isAccessTokenExpired());
        }

        @Test
        @DisplayName("should return true if accessToken is expired")
        public void t2() {
            session.setAccessTokenExpireDate(LocalDateTime.now().minusSeconds(1));
            assertTrue(session.isAccessTokenExpired());
        }

        @Test
        @DisplayName("should return true if session is expired")
        public void t3() {
            session.setExpireDate(LocalDate.now());
            assertTrue(session.isAccessTokenExpired());
        }
    }

    @Nested
    @DisplayName("expire()")
    class c3 {
        @Test
        @DisplayName("should update expire date")
        public void t1() {
            session.expire();
            assertEquals(LocalDate.now(), session.getExpireDate());
        }

        @Test
        @DisplayName("should not update expire date if sessions is already expired")
        public void t2() {
            var yesterday = LocalDate.now().minusDays(1);
            session.setExpireDate(yesterday);
            session.expire();
            assertEquals(yesterday, session.getExpireDate());
        }
    }

    @Test
    @DisplayName("refreshTokens() should generate new tokens and change expire dates")
    public void refreshTokens() {
        session.setAccessToken(ACCESS_TOKEN);
        session.setRefreshToken(REFRESH_TOKEN);

        var tomorrow = LocalDate.now().plusDays(1);
        session.setExpireDate(tomorrow);

        var lastHour = LocalDateTime.now().minusHours(1);
        session.setAccessTokenExpireDate(lastHour);

        session.refreshTokens();

        assertNotEquals(ACCESS_TOKEN, session.getAccessToken());
        assertNotEquals(REFRESH_TOKEN, session.getRefreshToken());

        assertEquals(192, session.getAccessToken().length());
        assertEquals(255, session.getRefreshToken().length());

        assertNotEquals(lastHour, session.getAccessTokenExpireDate());
        assertNotEquals(tomorrow, session.getExpireDate());
    }
}
