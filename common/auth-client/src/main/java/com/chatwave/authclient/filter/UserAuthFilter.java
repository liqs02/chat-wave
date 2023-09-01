package com.chatwave.authclient.filter;

import com.chatwave.authclient.client.AuthClient;
import com.chatwave.authclient.domain.UserAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
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
            } catch(Exception ignored) {}

            if(authentication != null) {
                authentication.getDetails().setRemoteAddress(request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        }
}
