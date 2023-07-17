package com.chatwave.authservice.controller;

import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.domain.dto.RefreshSessionRequest;
import com.chatwave.authservice.domain.dto.SessionResponse;
import com.chatwave.authservice.domain.dto.TokenSetResponse;
import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.session.SessionMapper;
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

    private User user;
    private Session session;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1);
        user.setPassword("pass");

        session = new Session(user);
        session.setId(2L);
        session.setRefreshToken("refresh");
        session.setAccessToken("access");
    }


    @Test
    @DisplayName("refreshTokens() should return token set")
    public void t1() {
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
    @DisplayName("getUserCurrentSessions() should return list of sessions")
    public void t2() {
        var sessionResponse = new SessionResponse(2L, session.getExpireDate(), session.getAccessTokenExpireDate(), session.getCreatedAt());

        when(
                service.getUserCurrentSessions(1)
        ).thenReturn(List.of(session));

        when(
                mapper.toSessionResponse(session)
        ).thenReturn(sessionResponse);

        var result = controller.getUserCurrentSessions(1);

        assertEquals(List.of(sessionResponse), result);
    }

    @Test
    @DisplayName("expireAllUserSessions() should expire all user sessions")
    public void t3() {
        controller.expireAllUserSessions(1);
        verify(service).expireAllUserSessions(1);
    }

    @Test
    @DisplayName("expireUserSession() should expire user session")
    @WithMockUser(username = "user", authorities = "SCOPE_ui")
    public void t4() {
        controller.expireUserSession(1,  2L);
        verify(service).expireUserSession(1, 2L);
    }
}