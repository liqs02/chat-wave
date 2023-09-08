package com.chatwave.authservice.unit.service;

import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.repository.UserRepository;
import com.chatwave.authservice.service.UserServiceImpl;
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

import static com.chatwave.authservice.utils.TestVariables.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authManager;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setLoginName(LOGIN_NAME);
        user.setPassword(PASSWORD);
    }

    @Nested
    @DisplayName("createUser(user)")
    class c1 {
        @Test
        @DisplayName("should create user, return new session")
         void t1() {
            when(
                    passwordEncoder.encode(PASSWORD)
            ).thenReturn(ENCODED);

            when(
                    userRepository.save( user )
            ).thenAnswer(i -> {
                var user = (User) i.getArgument(0);
                user.setId(1);
                return user;
            });

            var userWithId = user;
            userWithId.setId(USER_ID);


            when(
                    userRepository.save(user)
            ).thenReturn(userWithId);

            var result = userService.createUser(user);

            assertEquals(user, result);

            verify(passwordEncoder, times(1))
                    .encode(PASSWORD);

            verify(userRepository, times(1))
                    .save(user);
        }

        @Test
        @DisplayName("should fail if user already exists")
         void t2() {
            user.setPassword(null);

            when(userRepository.findByLoginName(LOGIN_NAME))
                    .thenReturn(Optional.of(user));

            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> userService.createUser(user)
            );

            assertTrue(thrown.getMessage().contains("loginName"));
            assertTrue(thrown.getMessage().contains("exists"));
        }
    }

    @Nested
    @DisplayName("authenticateUser(user)")
    class c2 {
        @Test
        @DisplayName("should authenticate a user and return a user")
         void t1() {
            when(
                    userRepository.findByLoginName(LOGIN_NAME)
            ).thenReturn(Optional.of(user));

            var result = userService.authenticateUser(user);

            assertEquals(user, result);

            verify(
                    authManager, times(1)
            ).authenticate(
                    new UsernamePasswordAuthenticationToken(LOGIN_NAME, PASSWORD)
            );
        }
    }

    @Nested
    @DisplayName("updateUserPassword(user, newPassword)")
    class c3 {
        @Test
        @DisplayName("should authenticate user and change password")
        void t1() {
            user.setId(1);

            when(
                    userRepository.findById(USER_ID)
            ).thenReturn(Optional.of(user));

            when(
                    passwordEncoder.encode(PASSWORD)
            ).thenReturn(ENCODED);


            userService.updateUserPassword(USER_ID, PASSWORD);

            var captor = ArgumentCaptor.forClass(User.class);

            verify(passwordEncoder, times(1))
                    .encode(PASSWORD);

            verify(userRepository, times(1))
                    .save(captor.capture());

            assertEquals(ENCODED, captor.getValue().getPassword());
        }
    }
}
