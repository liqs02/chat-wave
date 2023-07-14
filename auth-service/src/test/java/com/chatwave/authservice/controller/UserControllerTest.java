package com.chatwave.authservice.controller;

import com.chatwave.authservice.domain.Session;
import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.domain.UserMapper;
import com.chatwave.authservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.authservice.domain.dto.CreateUserRequest;
import com.chatwave.authservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController")
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objMapper;
    @MockBean
    private UserService service;
    @MockBean
    private UserMapper userMapper;

    @Nested
    @DisplayName("POST /users")
    class createUser {
        private ResultActions exec(CreateUserRequest createUserRequest) throws Exception {
            return mvc.perform(
                    post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(createUserRequest))
            );
        }

        @Test
        @DisplayName("should creates user and return access and refresh token")
        @WithMockUser(authorities = "SCOPE_server")
        public void t1() throws Exception {
            var createUserRequest = new CreateUserRequest(1, "Pass1234");

            var user = new User();
            var session = new Session(user);
            session.setAccessToken("access");
            session.setRefreshToken("refresh");

            when(
                userMapper.toUser(createUserRequest)
            ).thenReturn(user);

            when(
                    service.createUser(user)
            ).thenReturn(session);

            exec(createUserRequest)
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.refreshToken").value("refresh")
                )
                .andExpect(
                        jsonPath("$.accessToken").value("access")
                );
        }

        @Test
        @DisplayName("should send 403 status if any authorization data is not provided")
        public void t2() throws Exception {
            exec(new CreateUserRequest(1, "Pass1234"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("should send 400 status if password does not contain any number")
        @WithMockUser(authorities = "SCOPE_server")
        public void t3() throws Exception {
            exec(new CreateUserRequest(1, "Password"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /users/authenticate")
    class authenticateUser {
        private ResultActions exec(AuthenticateUserRequest authenticateUserRequest) throws Exception {
            return mvc.perform(
                    post("/users/authenticate")
                        .contentType(APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(authenticateUserRequest))
            );
        }

        @Test
        @DisplayName("should creates user and return access and refresh token")
        @WithMockUser(authorities = "SCOPE_server")
        public void t1() throws Exception {
            var authenticateUserRequest = new AuthenticateUserRequest(1, "Pass1234");

            var user = new User();
            var session = new Session(user);
            session.setAccessToken("access");
            session.setRefreshToken("refresh");

            when(
                userMapper.toUser(authenticateUserRequest)
            ).thenReturn(user);

            when(
                service.authenticateUser(user)
            ).thenReturn(session);

            exec(authenticateUserRequest)
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.refreshToken").value("refresh")
                )
                .andExpect(
                        jsonPath("$.accessToken").value("access")
                );
        }

        @Test
        @DisplayName("should send 403 status if any authorization data is not provided")
        public void t2() throws Exception {
            exec(new AuthenticateUserRequest(1, "Pass1234"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("should send 400 status if no password is provided")
        @WithMockUser(authorities = "SCOPE_server")
        public void t3() throws Exception {
            exec(new AuthenticateUserRequest(1, null))
                    .andExpect(status().isBadRequest());
        }
    }
}
