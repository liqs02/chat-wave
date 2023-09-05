package com.chatwave.authservice.unit.controller;

import com.chatwave.authservice.controller.SessionController;
import com.chatwave.authservice.domain.dto.RefreshSessionRequest;
import com.chatwave.authservice.domain.dto.SessionResponse;
import com.chatwave.authservice.domain.dto.TokenSetResponse;
import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.session.SessionMapper;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionController")
public class SessionControllerTest {
    @InjectMocks
    private SessionController controller;
    @Mock
    private SessionService service;
    @Mock
    private SessionMapper mapper;

    private Session session;

    @BeforeEach
    public void setup() {
        var user = new User();
        user.setId(1);
        user.setPassword("pass");

        session = new Session(user);
        session.setId(2L);
        session.setRefreshToken("refresh");
        session.setAccessToken("access");
    }


    @Test
    @DisplayName("refreshTokens() should return token set")
    public void refreshTokens() {
        when(
                service.refreshSession("token")
        ).thenReturn(session);

        var tokenSet = new TokenSetResponse("refresh", "access");

        when(
                mapper.toTokenSetResponse(session)
        ).thenReturn(tokenSet);

        var result = controller.refreshTokens(new RefreshSessionRequest("token"));

        assertEquals(tokenSet, result);
    }

    @Test
    @DisplayName("getNotExpiredSessionsByUserId() should return list of sessions")
    public void getUserCurrentSessions() {
        var sessionResponse = new SessionResponse(2L, session.getExpireDate(), session.getAccessTokenExpireDate(), session.getCreatedAt());

        when(
                service.getNotExpiredSessionsByUserId(1)
        ).thenReturn(List.of(session));

        when(
                mapper.toSessionResponse(session)
        ).thenReturn(sessionResponse);

        var result = controller.getActiveSessionsByUserId(1);

        assertEquals(List.of(sessionResponse), result);
    }

    @Test
    @DisplayName("expireSessionsByUserId() should expire all user sessions")
    public void expireAllUserSessions() {
        controller.expireUserSessions(1);
        verify(service).expireSessionsByUserId(1);
    }

    @Test
    @DisplayName("expireSession() should expire user session")
    @WithMockUser(username = "user", authorities = "SCOPE_ui")
    public void expireUserSession() {
        controller.expireSession(1,  2L);
        verify(service).expireSession(2L, 1);
    }
}
