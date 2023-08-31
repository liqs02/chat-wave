package com.chatwave.authservice.config;

import com.chatwave.authservice.domain.user.UserAuthentication;
import com.chatwave.authservice.repository.SessionRepository;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class UserAuthFilter extends OncePerRequestFilter {
        private final SessionRepository repository;

        @Override
        public void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            var authHeader = request.getHeader("User-Authorization");

            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            var accessToken = authHeader.substring(7);
            var optionalSession = repository.findNotExpiredByAccessToken(accessToken);

            if(optionalSession.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            var session = optionalSession.get();
            var userAuthentication = new UserAuthentication(session, request);

            SecurityContextHolder.getContext().setAuthentication(userAuthentication);
            log.trace("UserAuthFilter successfully authorized user. Session: " + userAuthentication.getDetails().getSessionId());
            filterChain.doFilter(request, response);
        }
}
