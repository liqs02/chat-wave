package com.chatwave.authservice.config;

import com.chatwave.authservice.domain.Session;
import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.service.SessionService;
import com.chatwave.authservice.service.SessionServiceImpl;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionAuthFilter")
public class SessionAuthFilterTest {
    @InjectMocks
    private SessionAuthFilter sessionAuthFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SessionService sessionService;

    @Test
    @DisplayName("should authenticate a user")
    public void t1() throws ServletException, IOException {
        var user = new User();
        user.setId(1);
        user.setPassword("pass");
        var userDetails = (UserDetails) user;

        var session = new Session(user);
        session.setId(2L);

        when(
                request.getHeader( "Authorization")
        ).thenReturn("Bearer token");

        when(
                sessionService.getActiveSession("token")
        ).thenReturn(Optional.of(session));


        sessionAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain)
                .doFilter(request, response);

        var authentication =  SecurityContextHolder.getContext().getAuthentication();

        assertEquals(1, authentication.getPrincipal());
        assertEquals(2L, authentication.getCredentials());
        assertEquals(0, authentication.getAuthorities().size());
    }

    @Test
    @DisplayName("should move to the next filter if the authorisation header is not specified")
    public void t2() throws ServletException, IOException {
        when(
                request.getHeader( "Authorization" )
        ).thenReturn(null);

        sessionAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

}
