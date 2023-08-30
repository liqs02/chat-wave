package com.chatwave.authservice.config;

import com.chatwave.authservice.domain.user.UserAuthentication;
import com.chatwave.authservice.repository.SessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserAuthFilter extends OncePerRequestFilter {
        private final SessionRepository repository;

        @Override
        public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            var authHeader = request.getHeader("User-Authorization");

            if(authHeader == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if(!authHeader.startsWith("Bearer ")) // todo: delete duplicated code (add logic to service)
                throw new ResponseStatusException(UNAUTHORIZED, "Invalid accessToken.");

            var accessToken = authHeader.substring(7);
            var optionalSession = repository.findNotExpiredByAccessToken(accessToken);

            if(optionalSession.isEmpty())
                throw new ResponseStatusException(UNAUTHORIZED, "Invalid accessToken.");

            var session = optionalSession.get();
            var userAuthentication = new UserAuthentication(session, request);

            SecurityContextHolder.getContext().setAuthentication(userAuthentication);
            log.trace("UserAuthFilter successfully authorized user. Session: " + userAuthentication.getDetails().getSessionId());
            filterChain.doFilter(request, response);
        }
}
