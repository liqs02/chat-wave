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

import java.io.IOException;

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

            var userAuthentication = authorizeByAccessToken(request);
            if (userAuthentication == null) {
                filterChain.doFilter(request, response);
                return;
            }

            SecurityContextHolder.getContext().setAuthentication(userAuthentication);
            log.trace("UserAuthFilter successfully authorized user. Session: " + userAuthentication.getDetails().getSessionId());
            filterChain.doFilter(request, response);
        }

        public UserAuthentication authorizeByAccessToken(HttpServletRequest request) {
            var authHeader = request.getHeader("User-Authorization"); // todo: change 'User-Authorization' to 'Authorization' header, REMEMBER that client authorization return 403/402 because "TOKEN IS NOT VALID JWT"

            if(authHeader == null || !authHeader.startsWith("Bearer "))
                return null;

            var accessToken = authHeader.substring(7);
            var optionalSession = repository.findNotExpiredByAccessToken(accessToken);

            if(optionalSession.isEmpty()) // todo add exception
                return null;
            var session = optionalSession.get();
            return new UserAuthentication(session, request);
        }
}
