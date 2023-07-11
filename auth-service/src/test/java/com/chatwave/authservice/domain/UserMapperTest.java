package com.chatwave.authservice.domain;

import com.chatwave.authservice.domain.dto.CreateUserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public@DisplayName("UserMapper")
class UserMapperTest {
    private final UserMapper mapper = UserMapper.INSTANCE;

    @Test
    @DisplayName("should map CreateUserRequest to User entity")
    public void CreateUserRequestToUser() {
        var createUserRequest = new CreateUserRequest( 1, "pass");

        var user = mapper.toUser(createUserRequest);

        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("pass", user.getPassword());
    }
}
