package com.chatwave.authclient.domain;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

public class UserAuthentication implements Authentication { // todo: create library for microservices and share there it
    Integer userId; // represents principals

    String accessToken; // represents credentials

    Collection<? extends GrantedAuthority> authorities;

    UserAuthenticationDetails details;


    /**
     * Updates information from request (for example remoteIp)
     */
    public void update(HttpServletRequest request) {
        details.update(request);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getCredentials() {
        return accessToken;
    }

    @Override
    public UserAuthenticationDetails getDetails() {
        return details;
    }

    @Override
    public Integer getPrincipal() {
        return userId;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}

    @Override
    public String getName() {
        return userId.toString();
    }

    @Override
    public boolean implies(Subject subject) {
        return Authentication.super.implies(subject);
    }
}
