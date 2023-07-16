package com.chatwave.authservice.domain.session;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

public class SessionAuthentication implements Authentication {
    Integer userId; // represents principals

    String accessToken; // represents credentials

    Collection<? extends GrantedAuthority> authorities;

    SessionAuthenticationDetails details;

    public SessionAuthentication(Session session, HttpServletRequest request) {
        this.userId = session.getUser().getId();
        this.accessToken = session.getAccessToken();
        this.authorities = session.getUser().getAuthorities();
        this.details = new SessionAuthenticationDetails(session, request);
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
    public SessionAuthenticationDetails getDetails() {
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
