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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    @Mock
    private AuthenticationManager authManager;

    private final UUID uuid0 = UUID.fromString("00000000-0000-0000-0000-000000000000");

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
            refreshToken.setId( uuid0 );

            when(
                passwordEncoder.encode("pass")
            ).thenReturn("encoded");

            when(
                refreshTokenService.create( user )
            ).thenReturn(refreshToken);

            when(
                    jwtService.generateToken( user )
            ).thenReturn("accessToken");

            var tokens = service.create(user);

            assertEquals("accessToken", tokens.getAccessToken());
            assertEquals(
                    uuid0,
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

            when(repository.findById( eq(1) ))
                    .thenReturn(Optional.of(user));

            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> service.create(user)
            );

            assertTrue(thrown.getMessage().contains("id"));
            assertTrue(thrown.getMessage().contains("exists"));
        }
    }

    @Nested
    @DisplayName("authenticate( user )")
    class authenticate {
        @Test
        @DisplayName("should authenticate a user and return tokens")
        public void t1() {
            var user = new User();
            user.setId(1);
            user.setPassword("pass");

            var refreshToken = new RefreshToken();
            refreshToken.setId(uuid0);

            when(
                    refreshTokenService.create(user)
            ).thenReturn(refreshToken);

            when(
                    jwtService.generateToken(user)
            ).thenReturn("accessToken");

            var tokens = service.authenticate(user);

            assertEquals("accessToken", tokens.getAccessToken());

            assertEquals(
                    uuid0,
                    tokens.getRefreshToken()
            );

            verify(
                    authManager, times(1)
            ).authenticate(
                    new UsernamePasswordAuthenticationToken(
                            1,
                           "pass"
                    )
            );
        }
    }

    @Nested
    @DisplayName("refreshToken( refreshTokenId )")
    class refreshToken {
        @Test
        @DisplayName("should return new jwt and refresh token")
        public void t1() {
            var refreshToken = new RefreshToken();
            var user = new User();
            refreshToken.setId(UUID.randomUUID());
            refreshToken.setUser(user);

            when(
                    refreshTokenService.refresh(uuid0)
            ).thenReturn(refreshToken);

            when(
                    jwtService.generateToken(user)
            ).thenReturn("JWT");

            var result = service.refreshToken(uuid0);

            assertNotNull(result);
            assertEquals(refreshToken.getId(), result.getRefreshToken());
            assertEquals("JWT", result.getAccessToken());
        }
    }

}
