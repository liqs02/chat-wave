package com.chatwave.authservice.unit.service;

import com.chatwave.authservice.config.UserAuthFilter;
import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.domain.user.UserAuthentication;
import com.chatwave.authservice.repository.UserRepository;
import com.chatwave.authservice.service.SessionService;
import com.chatwave.authservice.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
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
    private SessionService sessionService;
    @Mock
    private AuthenticationManager authManager;
    @Mock
    private UserAuthFilter userAuthFilter;
    private User user;
    private Session session;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1);
        user.setPassword("pass");

        session = new Session(user);
        session.setId(1L);
    }

    @Nested
    @DisplayName("createUser( user )")
    class createUser {
        @Test
        @DisplayName("should create user, return new session")
         void t1() {
            when(
                passwordEncoder.encode("pass")
            ).thenReturn("encoded");

            when(
                sessionService.createSession(user)
            ).thenReturn(session);

            var result = service.createUser(user);

            assertEquals(session, result);

            var captor = ArgumentCaptor.forClass(User.class);

            verify(passwordEncoder, times(1))
                    .encode("pass");

            verify(repository, times(1))
                    .save(captor.capture());

            assertEquals("encoded", captor.getValue().getPassword());
        }

        @Test
        @DisplayName("should fail if user already exists")
         void t2() {
            user.setPassword(null);

            when(repository.findById( eq(1) ))
                    .thenReturn(Optional.of(user));

            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> service.createUser(user)
            );

            assertTrue(thrown.getMessage().contains("id"));
            assertTrue(thrown.getMessage().contains("exists"));
        }
    }

    @Nested
    @DisplayName("authenticateUser( user )")
    class authenticateUser {
        @Test
        @DisplayName("should authenticate a user and return new session")
         void t1() {
            when(
                    sessionService.createSession(user)
            ).thenReturn(session);

            var result = service.authenticateUser(user);

            assertEquals(1L, result.getId());

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
    @DisplayName("patchUserPassword( user, new password )")
    class patchUserPassword {
        @Test
        @DisplayName("should authenticate user and change password")
        void t1() {
            when(
                    repository.findById(1)
            ).thenReturn(Optional.of(user));

            when(
                    passwordEncoder.encode("new")
            ).thenReturn("encoded");

            service.patchUserPassword(user, "new");

            var captor = ArgumentCaptor.forClass(User.class);

            verify(passwordEncoder, times(1))
                    .encode("new");

            verify(repository, times(1))
                    .save(captor.capture());

            assertEquals("encoded", captor.getValue().getPassword());
        }
    }
}
