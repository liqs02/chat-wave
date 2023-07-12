package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.RefreshToken;
import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.repository.RefreshTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService")
public class RefreshTokenServiceTest {
    @InjectMocks
    private RefreshTokenServiceImpl service;
    @Mock
    private RefreshTokenRepository repository;

    private final UUID uuid0 = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private final UUID uuid1 = UUID.fromString("10000000-0000-0000-0000-000000000000");

    @Nested
    @DisplayName("create( user )")
    class create {
        @Test
        @DisplayName("should create and return refresh token")
        public void t1() {
            // Arrange
            var user = new User();
            user.setId(1);
            user.setPassword("pass");

            when(
                    repository.save( isA(RefreshToken.class) )
            )
            .thenAnswer(invocation -> {
                RefreshToken token = invocation.getArgument(0);
                token.setId(uuid0);
                return token;
            });

            // Act
            var token = service.create(user);

            // Assert
            verify(repository, times(1))
                    .save( isA(RefreshToken.class) );


            assertNotNull(token);

            assertEquals(uuid0, token.getId());

            assertNotNull(token.getUser());
            assertEquals(1, token.getUser().getId());

            assertTrue(
                    token.getExpirationAt()
                    .isEqual(
                            LocalDate.now().plusMonths(1)
                    )
            );
        }
    }

    @Nested
    @DisplayName("refresh( tokenId )")
    class refresh {
        @Test
        @DisplayName("should refresh token")
        public void t1() throws NoSuchFieldException, IllegalAccessException {
            // Arrange
            var user = new User();
            user.setId(1);

            var token = new RefreshToken();
            token.setId(uuid0);
            token.setUser(user);

            var expField = RefreshToken.class.getDeclaredField("createdAt");
            expField.setAccessible(true);
            expField.set(token, LocalDateTime.now().minusMinutes(2));

            when(
                    repository.findById(uuid0)
            ).thenReturn( Optional.of(token) );

            when(
                    repository.save( isA(RefreshToken.class) )
            )
            .thenAnswer(i -> {
                var newToken = (RefreshToken) i.getArguments()[0];
                newToken.setId(uuid1);
                return newToken;
            });

            // Act
            var result = service.refresh(uuid0);

            // Assert
            verify(repository, times(1))
                    .findById(uuid0);

            verify(repository, times(1))
                    .delete(token);

            verify(repository, times(1))
                    .save(isA(RefreshToken.class));

            assertNotNull(result);

            assertEquals(
                    uuid1,
                    result.getId()
            );

            assertNotNull(result.getUser());
            assertEquals(1, result.getUser().getId());

            assertTrue(
                    result.getExpirationAt().isEqual(
                            LocalDate.now().plusMonths(1)
                    )
            );
        }

        @Test
        @DisplayName("should throw BAD_REQUEST if token with given ID does not exist")
        public void t2() {
            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> service.refresh(UUID.randomUUID())
            );

            assertEquals(BAD_REQUEST, thrown.getStatusCode());
        }

        @Test
        @DisplayName("should throw BAD_REQUEST and delete token if token is inactive")
        public void t3() {
            var user = new User();
            user.setId(1);

            var token = new RefreshToken();
            token.setId(uuid0);
            token.setUser(user);

            when(
                    repository.findById(uuid0)
            ).thenReturn( Optional.of(token) );

            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> service.refresh(uuid0)
            );

            verify(
                    repository, times(1)
            ).delete(token);

            assertEquals(BAD_REQUEST, thrown.getStatusCode());
        }
    }

    @Nested
    @DisplayName("invalidate( tokenId )")
    class invalidate {
        @Test
        @DisplayName("should invalidate token")
        public void t1() {
            var token = new RefreshToken();
            token.setId(uuid0);

            when(
                    repository.findById(uuid0)
            ).thenReturn(Optional.of(token));

            service.invalidate(uuid0);

            verify(repository, times(1))
                    .delete(token);
        }

        @Test
        @DisplayName("should throw BAD_REQUEST if token with given ID does not exist")
        public void t2() {
            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> service.refresh(UUID.randomUUID())
            );

            assertEquals(BAD_REQUEST, thrown.getStatusCode());
        }
    }

}
