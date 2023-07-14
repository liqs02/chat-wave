package com.chatwave.authservice.config;

import com.chatwave.authservice.service.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.apache.commons.lang.Validate.notNull;

@Component
@Slf4j
public class SessionAuthFilter extends OncePerRequestFilter {
        private SessionService sessionService;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            var authHeader = request.getHeader("Authorization");

            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            var accessToken = authHeader.substring(7);
            var optionalSession = sessionService.getActiveSession(accessToken);

            if(optionalSession.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
                var session = optionalSession.get();
                var user = session.getUser();

                var authToken = new UsernamePasswordAuthenticationToken(
                        user.getId(), session.getId(), user.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.trace("Session auth filter successfully authorized user. SessionId: " + session.getId());
                filterChain.doFilter(request, response);

            }
        }

        @Autowired
        public void setSessionService(SessionService sessionService) {
            notNull(sessionService, "SessionService can not be null!");
            this.sessionService = sessionService;
        }
}
