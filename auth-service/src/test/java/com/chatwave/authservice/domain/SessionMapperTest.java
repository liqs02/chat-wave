package com.chatwave.authservice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public@DisplayName("SessionMapper")
class SessionMapperTest {
    private final SessionMapper mapper = SessionMapper.INSTANCE;

    @Test
    @DisplayName("should map session entity to TtokenSetResponse")
    public void SessionToTokenSetResponse() {
        var session = new Session();
        session.setRefreshToken("refresh");
        session.setAccessToken("access");

        var tokenSet = mapper.toTokenSetResponse(session);

        assertEquals("refresh", tokenSet.refreshToken());
        assertEquals("access", tokenSet.accessToken());
    }
}
