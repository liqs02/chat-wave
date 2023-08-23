package com.chatwave.authservice.config;

import com.chatwave.authservice.domain.user.UserAuthentication;
import com.chatwave.authservice.repository.SessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
@Setter(onMethod_=@Autowired)
@Slf4j
public class UserAuthFilter extends OncePerRequestFilter {
        private SessionRepository repository;

        @Override
        public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            var userAuthentication = getUserAuthentication(request);
            if (userAuthentication == null) {
                filterChain.doFilter(request, response);
                return;
            }

            SecurityContextHolder.getContext().setAuthentication(userAuthentication);
            log.trace("UserAuthFilter successfully authorized user. Session: " + userAuthentication.getDetails().getSessionId());
            filterChain.doFilter(request, response);
        }

        public UserAuthentication getUserAuthentication(HttpServletRequest request) {
            var authHeader = request.getHeader("User-Authorization");

            if(authHeader == null)
                return null;

            if(!authHeader.startsWith("Bearer "))
                throw new ResponseStatusException(UNAUTHORIZED, "Invalid accessToken.");

            var accessToken = authHeader.substring(7);
            var optionalSession = repository.findNotExpiredByAccessToken(accessToken);

            if(optionalSession.isEmpty())
                throw new ResponseStatusException(UNAUTHORIZED, "Invalid accessToken.");

            var session = optionalSession.get();
            return new UserAuthentication(session, request);
        }
}
