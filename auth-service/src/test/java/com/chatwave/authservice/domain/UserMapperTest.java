package com.chatwave.authservice.domain;

import com.chatwave.authservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.authservice.domain.dto.PatchPasswordRequest;
import com.chatwave.authservice.domain.dto.CreateUserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("UserMapper")
public class UserMapperTest {
    private final UserMapper mapper = UserMapper.INSTANCE;

    @Test
    @DisplayName("should map createUserRequest to user entity")
    public void createUserRequestToUser() {
        var createUserRequest = new CreateUserRequest( 1, "pass");

        var user = mapper.toUser(createUserRequest);

        assertEquals(1, user.getId());
        assertEquals("pass", user.getPassword());
    }

    @Test
    @DisplayName("should map authenticateUserRequest to user entity")
    public void authenticateUserRequestToUser() {
        var authenticateUserRequest = new AuthenticateUserRequest( 1, "pass");

        var user = mapper.toUser(authenticateUserRequest);

        assertEquals(1, user.getId());
        assertEquals("pass", user.getPassword());
    }

    @Test
    @DisplayName("should map userId and PatchPasswordRequest to user entity")
    public void changePasswordRequestToUser() {
        var changePasswordRequest = new PatchPasswordRequest("pass", "new");
        var user = mapper.toUser(1, changePasswordRequest);

        assertEquals(1, user.getId());
        assertEquals("pass", user.getPassword());
    }
}
