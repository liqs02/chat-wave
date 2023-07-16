package com.chatwave.authservice.config;

import com.chatwave.authservice.domain.session.SessionAuthentication;
import com.chatwave.authservice.repository.SessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.apache.commons.lang.Validate.notNull;

@Component
@Slf4j
public class SessionAuthFilter extends OncePerRequestFilter {
        private SessionRepository repository;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            var authHeader = request.getHeader("Authorization");

            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            var accessToken = authHeader.substring(7);
            var optionalSession = repository.findNotExpiredByAccessToken(accessToken);

            if(optionalSession.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
                var session = optionalSession.get();

                var sessionAuth = new SessionAuthentication(session, request);
                SecurityContextHolder.getContext().setAuthentication(sessionAuth);

                log.trace("Session auth filter successfully authorized user. Session: " + session);
                filterChain.doFilter(request, response);
            }
        }

        @Autowired
        public void setRepository(SessionRepository repository) {
            notNull(repository, "SessionRepository can not be null!");
            this.repository = repository;
        }
}
