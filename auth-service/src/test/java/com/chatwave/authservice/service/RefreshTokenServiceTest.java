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

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService")
public class RefreshTokenServiceTest {
    @InjectMocks
    private RefreshTokenServiceImpl service;
    @Mock
    private RefreshTokenRepository repository;

    @Nested
    @DisplayName("create( user )")
    class create {
        @Test
        @DisplayName("should create and return refresh token")
        public void t1() {
            var user = new User();
            user.setId(1);
            user.setPassword("pass");

            when(
                    repository.save( isA(RefreshToken.class) )
            )
            .thenAnswer(invocation -> {
                RefreshToken token = invocation.getArgument(0);
                token.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
                return token;
            });

            var token = service.create(user);


            verify(repository, times(1))
                    .save( isA(RefreshToken.class) );


            assertNotNull(token);

            assertEquals("00000000-0000-0000-0000-000000000000", token.getId().toString());

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
}
