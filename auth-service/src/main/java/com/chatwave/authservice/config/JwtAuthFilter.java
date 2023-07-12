package com.chatwave.authservice.config;

import com.chatwave.authservice.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.apache.commons.lang.Validate.notNull;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
        private JwtService jwtService;

        private UserDetailsService userDetailsService;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            var authHeader = request.getHeader("Authorization");

            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            var token = authHeader.substring(7);
            var username = jwtService.extractUsername(token);

            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = userDetailsService.loadUserByUsername(username);

                if(jwtService.isTokenValid(token, userDetails)) {

                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails.getUsername(), null, userDetails.getAuthorities()
                    );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.trace("JWT auth filter successfully authorized user: " + username);
                    filterChain.doFilter(request, response);
                }
            }
        }

        @Autowired
        public void setUserDetailsService(UserDetailsService userDetailsService) {
            notNull(userDetailsService, "UserDetailsService can not be null!");
            this.userDetailsService = userDetailsService;
        }

        @Autowired
        public void setJwtService(JwtService jwtService) {
            notNull(jwtService, "JwtService can not be null!");
            this.jwtService = jwtService;
        }
}
