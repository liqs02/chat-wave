package com.chatwave.authservice.unit.domain;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.session.SessionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.chatwave.authservice.utils.TestVariables.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("SessionMapper")
class SessionMapperTest {
    private final SessionMapper mapper = SessionMapper.INSTANCE;
    private Session session;

    @BeforeEach
    void setup() {
        session = new Session();
        session.setId(SESSION_ID);
        session.setRefreshToken(REFRESH_TOKEN);
        session.setAccessToken(ACCESS_TOKEN);
        session.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("toTokenSetResponse(Session) should map session entity to TokenSetResponse")
    void toTokenSetResponse() {
        var result = mapper.toTokenSetResponse(session);

        assertEquals(REFRESH_TOKEN, result.refreshToken());
        assertEquals(ACCESS_TOKEN, result.accessToken());
    }

    @Test
    @DisplayName("toSessionResponse(Session) should map session entity to GetSessionResponse")
    void toSessionResponse() {
        var result = mapper.toSessionResponse(session);

        assertEquals(SESSION_ID, result.id());
        assertEquals(session.getExpireDate(), result.expireDate());
        assertEquals(session.getAccessTokenExpireDate(), result.accessTokenExpireDate());
        assertEquals(session.getCreatedAt(), result.createdAt());
    }
}
