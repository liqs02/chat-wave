package com.chatwave.authservice.controller;

import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.domain.UserMapper;
import com.chatwave.authservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.authservice.domain.dto.CreateUserRequest;
import com.chatwave.authservice.domain.dto.TokenSetResponse;
import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.session.SessionMapper;
import com.chatwave.authservice.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

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
    @DisplayName("createUser() should creates user and return access and refresh token")
    public void t1() {
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

        Assertions.assertEquals("refresh", result.refreshToken());
        Assertions.assertEquals("access", result.accessToken());
    }

    @Test
    @DisplayName("authenticateUser() should creates user and return access and refresh token")
    public void t2() {
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

        Assertions.assertEquals("refresh", result.refreshToken());
        Assertions.assertEquals("access", result.accessToken());
    }
}