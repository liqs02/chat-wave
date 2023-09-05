package com.chatwave.authservice.unit.controller;

import com.chatwave.authservice.controller.UserController;
import com.chatwave.authservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.authservice.domain.dto.CreateUserRequest;
import com.chatwave.authservice.domain.dto.PatchUserRequest;
import com.chatwave.authservice.domain.dto.TokenSetResponse;
import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.session.SessionMapper;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.domain.user.UserAuthentication;
import com.chatwave.authservice.domain.user.UserMapper;
import com.chatwave.authservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController")
public class UserControllerTest {
    @InjectMocks
    private UserController controller;
    @Mock
    private UserService service;
    @Mock
    private UserMapper mapper;
    @Mock
    private SessionMapper sessionMapper;

    private Session session;
    private User user;
    private TokenSetResponse tokenSetResponse;

    @BeforeEach
    public void setup() {
        user = new User();
        session = new Session(user);
        session.setAccessToken("access");
        session.setRefreshToken("refresh");

        tokenSetResponse = new TokenSetResponse("refresh", "access");
    }

    @Test
    @DisplayName("getUserAuthentication() should invoke service.getUserAuthentication() and return it")
    public void getUserAuthentication() {
        var request = mock(HttpServletRequest.class);
        var userAuthentication = mock(UserAuthentication.class);

        when(
                service.getUserAuthentication(request)
        ).thenReturn(userAuthentication);

        var result = controller.getUserAuthentication(request);

        assertEquals(userAuthentication, result);
    }

    @Test
    @DisplayName("createUser() should creates user and return accessToken and refreshToken")
    public void createUser() {
        var createUserRequest = new CreateUserRequest(1, "Pass1234");

        when(
                mapper.toUser(createUserRequest)
        ).thenReturn(user);

        when(
                service.createUser(user)
        ).thenReturn(session);

        when(
                sessionMapper.toTokenSetResponse(session)
        ).thenReturn(tokenSetResponse);

        var result = controller.createUser(createUserRequest);

        assertEquals("refresh", result.refreshToken());
        assertEquals("access", result.accessToken());
    }

    @Test
    @DisplayName("authenticateUser() should creates user and return accessToken and refreshToken")
    public void authenticateUser() {
        var authenticateUserRequest = new AuthenticateUserRequest(1, "Pass1234");

        var user = new User();
        var session = new Session(user);
        session.setAccessToken("access");
        session.setRefreshToken("refresh");

        when(
                mapper.toUser(authenticateUserRequest)
        ).thenReturn(user);

        when(
                service.authenticateUser(user)
        ).thenReturn(session);

        when(
                sessionMapper.toTokenSetResponse(session)
        ).thenReturn(tokenSetResponse);

        var result = controller.authenticateUser(authenticateUserRequest);

        assertEquals("refresh", result.refreshToken());
        assertEquals("access", result.accessToken());
    }

    @Test
    @DisplayName("patchUser() should change user's password")
    public void patchUserPassword() {
        var changePasswordRequest = new PatchUserRequest("pass", "new");

        var user = new User();

        when(
                mapper.toUser(1, changePasswordRequest)
        ).thenReturn(user);

        controller.patchUser(1, changePasswordRequest);

        verify(
                service,
                times(1)
        ).patchUser(user, "new");
    }
}