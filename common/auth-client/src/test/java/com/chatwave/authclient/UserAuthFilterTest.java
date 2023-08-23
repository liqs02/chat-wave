package com.chatwave.authclient;

import com.chatwave.authclient.client.AuthService;
import com.chatwave.authclient.domain.UserAuthentication;
import com.chatwave.authclient.filter.UserAuthFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserAuthFilter")
public class UserAuthFilterTest {
    @InjectMocks
    private UserAuthFilter userAuthFilter;
    @Mock
    private AuthService authService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private UserAuthentication userAuthentication;

    @Test
    @DisplayName("should authenticate a user in auth-service")
    void t1() throws ServletException, IOException {
        when(
                request.getHeader("User-Authorization")
        ).thenReturn("Bearer token");

        when(
                authService.getUserAuthentication("Bearer token")
        ).thenReturn(userAuthentication);

        userAuthFilter.doFilterInternal(request, response, filterChain);

        assertEquals(userAuthentication, SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("should invoke doFilter() if User-Authorization header is not provided")
    void t2() throws ServletException, IOException {
        userAuthFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
