package com.chatwave.authservice.config;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.domain.user.UserAuthentication;
import com.chatwave.authservice.repository.SessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserAuthFilter")
public class UserAuthFilterTest {
    @InjectMocks
    private UserAuthFilter userAuthFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SessionRepository repository;

    @Nested
    @DisplayName("doFilterInternal(request, response, filterChain)")
    class doFilterInternal {
        @Test
        @DisplayName("should authenticate a user")
        public void t1() throws ServletException, IOException {
            var user = new User();
            user.setId(1);
            user.setPassword("pass");

            var session = new Session(user);
            session.setId(2L);
            session.setAccessToken("access");

            when(
                    request.getHeader( "Authorization")
            ).thenReturn("Bearer token");

            when(
                    repository.findNotExpiredByAccessToken("token")
            ).thenReturn(Optional.of(session));

            userAuthFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain)
                    .doFilter(request, response);

            var authentication = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();

            // check principals
            assertEquals(1, authentication.getPrincipal());
            assertEquals("1", authentication.getName());
            assertEquals(0, authentication.getAuthorities().size());
            // check credentials
            assertEquals("access", authentication.getCredentials());
            // check details
            assertEquals(2L, authentication.getDetails().getSessionId());
        }

        @Test
        @DisplayName("should move to the next filter if the authorization header is not specified")
        public void t2() throws ServletException, IOException {
            userAuthFilter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);
        }
    }


}
