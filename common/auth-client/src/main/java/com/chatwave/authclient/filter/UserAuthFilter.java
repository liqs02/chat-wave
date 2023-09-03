package com.chatwave.authclient.filter;

import com.chatwave.authclient.client.AuthClient;
import com.chatwave.authclient.domain.UserAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class UserAuthFilter extends OncePerRequestFilter {
    private final AuthClient authClient;

    @Override
    public void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        var authHeader = request.getHeader("User-Authorization");

        if(authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        var authentication = (UserAuthentication) null;
        try {
            authentication = authClient.getUserAuthentication(authHeader);
            log.debug("User has been successfully authenticated");
        } catch(Exception e) {
            log.debug("User has not been authenticated: " + e.getMessage());
        }

        if(authentication != null) {
            authentication.getDetails().setRemoteAddress(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
