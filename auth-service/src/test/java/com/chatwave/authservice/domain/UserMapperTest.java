package com.chatwave.authservice.domain;

import com.chatwave.authservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.authservice.domain.dto.CreateUserRequest;
import com.chatwave.authservice.domain.dto.PatchPasswordRequest;
import com.chatwave.authservice.domain.user.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("UserMapper")
public class UserMapperTest {
    private final UserMapper mapper = UserMapper.INSTANCE;

    @Test
    @DisplayName("toUser(CreateUserRequest) should map createUserRequest to user entity")
    public void toUser() {
        var createUserRequest = new CreateUserRequest( 1, "pass");

        var user = mapper.toUser(createUserRequest);

        assertEquals(1, user.getId());
        assertEquals("pass", user.getPassword());
    }

    @Test
    @DisplayName("toUser(AuthenticateUserRequest) should map authenticateUserRequest to user entity")
    public void toUser2() {
        var authenticateUserRequest = new AuthenticateUserRequest( 1, "pass");

        var user = mapper.toUser(authenticateUserRequest);

        assertEquals(1, user.getId());
        assertEquals("pass", user.getPassword());
    }

    @Test
    @DisplayName("toUser(userId, PatchPasswordRequest) should map userId and PatchPasswordRequest to user entity")
    public void toUser3() {
        var patchPasswordRequest = new PatchPasswordRequest("pass", "new");
        var user = mapper.toUser(1, patchPasswordRequest);

        assertEquals(1, user.getId());
        assertEquals("pass", user.getPassword());
    }
}
