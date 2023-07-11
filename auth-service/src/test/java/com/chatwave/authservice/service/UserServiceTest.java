package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.RefreshToken;
import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl service;
    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private JwtService jwtService;

    @Nested
    @DisplayName("create( user )")
    class create {
        @Test
        @DisplayName("should create user")
        public void t1() {
            var user = new User();
            user.setId(1);
            user.setPassword("pass");

            var refreshToken = new RefreshToken();
            refreshToken.setId( UUID.fromString("00000000-0000-0000-0000-000000000000") );

            when(
                passwordEncoder.encode("pass")
            ).thenReturn("encoded");

            when(
                refreshTokenService.create( eq(user) )
            ).thenReturn(refreshToken);

            when(
                    jwtService.generateToken( eq(user) )
            ).thenReturn("accessToken");

            var tokens = service.create(user);

            assertEquals("accessToken", tokens.getAccessToken());
            assertEquals(
                    UUID.fromString("00000000-0000-0000-0000-000000000000"),
                    tokens.getRefreshToken()
                    );

            var captor = ArgumentCaptor.forClass(User.class);

            verify(passwordEncoder, times(1))
                    .encode("pass");

            verify(repository, times(1))
                    .save(captor.capture());

            assertEquals("encoded", captor.getValue().getPassword());
        }

        @Test
        @DisplayName("should fail if user already exists")
        public void t2() {
            var user = new User();
            user.setId(1);

            when(repository.findById(1))
                    .thenReturn(Optional.of(user));

            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> service.create(user)
            );

            assertTrue(thrown.getMessage().contains("id"));
            assertTrue(thrown.getMessage().contains("exists"));
        }
    }

}
