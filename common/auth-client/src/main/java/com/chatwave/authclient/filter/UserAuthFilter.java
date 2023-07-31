package com.chatwave.authclient.filter;

import com.chatwave.authclient.client.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Setter(onMethod_=@Autowired)
public class UserAuthFilter extends OncePerRequestFilter {
        AuthService authService;

        @Override
        public void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
            var authHeader = request.getHeader("User-Authorization");

            if(authHeader == null) {
                filterChain.doFilter(request, response);
                return;
            }

            var authentication = authService.getUserAuthentication(authHeader);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        }
}
