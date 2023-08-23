package com.chatwave.authservice.config;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.UserAuthentication;
import com.chatwave.authservice.repository.SessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
@Setter(onMethod_=@Autowired)
@Slf4j
public class UserAuthFilter extends OncePerRequestFilter {
        private SessionRepository repository;

        @Override
        public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            var authHeader = request.getHeader("Authorization");
            if(authHeader == null) {
                filterChain.doFilter(request, response);
                return;
            }

            var optionalSession = getSession(authHeader);
            if (optionalSession.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            var userAuthentication = new UserAuthentication(optionalSession.get(), request);

            SecurityContextHolder.getContext().setAuthentication(userAuthentication);
            log.trace("UserAuthFilter successfully authorized user. Session: " + userAuthentication.getDetails().getSessionId());
            filterChain.doFilter(request, response);
        }

        public Optional<Session> getSession(String token) {
            if(!token.startsWith("Bearer "))
                return Optional.empty();

            var accessToken = token.substring(7);
            return repository.findNotExpiredByAccessToken(accessToken);
        }
}
