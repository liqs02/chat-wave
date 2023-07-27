package com.chatwave.accountservice.config;

import com.chatwave.accountservice.client.AuthService;
import feign.FeignException;
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
        AuthService authService;

        @Override
        public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException { // todo: create common library for microservices
            var authHeader = request.getHeader("User-Authorization");

            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            try {
                var authentication = authService.getUserAuthentication(authHeader);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            } catch(FeignException.BadRequest e) {
                throw new ResponseStatusException(UNAUTHORIZED, "Invalid access token.");
            }
        }
}
