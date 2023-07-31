package com.chatwave.authservice.domain;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.session.SessionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("SessionMapper")
class SessionMapperTest {
    private final SessionMapper mapper = SessionMapper.INSTANCE;
    private Session session;

    @BeforeEach
    void setup() {
        session = new Session();
        session.setId(1L);
        session.setRefreshToken("refresh");
        session.setAccessToken("access");
        session.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("should map session entity to TokenSetResponse")
    void SessionToTokenSetResponse() {
        var result = mapper.toTokenSetResponse(session);

        assertEquals("refresh", result.refreshToken());
        assertEquals("access", result.accessToken());
    }

    @Test
    @DisplayName("should map session entity to SessionResponse")
    void SessionToSessionResponse() {
        var result = mapper.toSessionResponse(session);

        assertEquals(1L, result.id());
        assertEquals(session.getExpireDate(), result.expireDate());
        assertEquals(session.getAccessTokenExpireDate(), result.accessTokenExpireDate());
        assertEquals(session.getCreatedAt(), result.createdAt());
    }
}
