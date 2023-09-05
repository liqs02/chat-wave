package com.chatwave.authclient.filter;

import com.chatwave.authclient.client.AuthClient;
import feign.FeignException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@RequiredArgsConstructor
@Slf4j
public class UserAuthFilter extends OncePerRequestFilter {
    private final AuthClient authClient;

    @Override
    public void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        var authHeader = request.getHeader("User-Authorization");

        if(authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            var authentication = authClient.getUserAuthentication(authHeader);
            authentication.getDetails().setRemoteAddress(request.getRemoteAddr());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("User has been successfully authenticated");
        } catch(FeignException.Unauthorized e) {
            log.debug("User send invalid accessToken: " + e.getMessage());
            response.sendError(SC_UNAUTHORIZED, "Invalid accessToken");
            return;
        } catch(Exception e) {
            response.sendError(SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
