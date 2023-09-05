package com.chatwave.authservice.unit.domain;

import com.chatwave.authservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.authservice.domain.dto.CreateUserRequest;
import com.chatwave.authservice.domain.dto.PatchUserRequest;
import com.chatwave.authservice.domain.user.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("UserMapper")
public class UserMapperTest {
    private final UserMapper mapper = UserMapper.INSTANCE;

    @Test
    @DisplayName("toUser(CreateUserRequest) should map createUserRequest to user entity")
    public void t1() {
        var createUserRequest = new CreateUserRequest( 1, "pass");

        var user = mapper.toUser(createUserRequest);

        assertEquals(1, user.getId());
        assertEquals("pass", user.getPassword());
    }

    @Test
    @DisplayName("toUser(AuthenticateUserRequest) should map authenticateUserRequest to user entity")
    public void t2() {
        var authenticateUserRequest = new AuthenticateUserRequest( 1, "pass");

        var user = mapper.toUser(authenticateUserRequest);

        assertEquals(1, user.getId());
        assertEquals("pass", user.getPassword());
    }

    @Test
    @DisplayName("toUser(userId, PatchUserRequest) should map userId and PatchUserRequest to user entity")
    public void t3() {
        var patchPasswordRequest = new PatchUserRequest("pass", "new");
        var user = mapper.toUser(1, patchPasswordRequest);

        assertEquals(1, user.getId());
        assertEquals("pass", user.getPassword());
    }
}
