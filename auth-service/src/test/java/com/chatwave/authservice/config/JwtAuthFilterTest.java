package com.chatwave.authservice.config;

import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthFilter")
public class JwtAuthFilterTest {
    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Test
    @DisplayName("should")
    public void t1() throws ServletException, IOException {
        var user = new User();
        user.setId(1);
        user.setPassword("pass");
        var userDetails = (UserDetails) user;

        when(
                request.getHeader( eq("Authorization") )
        ).thenReturn("Bearer token");

        when(
                jwtService.extractUsername( eq("token") )
        ).thenReturn("1");

        when(
                userDetailsService.loadUserByUsername( eq("1") )
        ).thenReturn(userDetails);

        when(
                jwtService.isTokenValid(
                                eq("token"), eq(userDetails)
                        )
        ).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain)
                .doFilter(request, response);

        verify(jwtService)
                .isTokenValid( eq("token") , eq(userDetails) );

        var authentication =  SecurityContextHolder.getContext().getAuthentication();

        assertEquals(
                "1",
                authentication.getPrincipal()
        );
    }

    @Test
    @DisplayName("should move to the next filter if the authorisation header is not specified")
    public void t2() throws ServletException, IOException {
        when(
                request.getHeader("Authorization")
        ).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

}
