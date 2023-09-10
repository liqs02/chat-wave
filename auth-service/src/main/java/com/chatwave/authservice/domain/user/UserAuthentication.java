package com.chatwave.authservice.domain.user;

import com.chatwave.authservice.domain.session.Session;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.List;

public class UserAuthentication implements Authentication {
    private final Integer userId; // represents principals
    private final String userLoginName;
    private final String accessToken; // represents credentials

    private final List<GrantedAuthority> authorities;

    private final UserAuthenticationDetails details;

    public UserAuthentication(Session session, HttpServletRequest request) {
        this.userId = session.getUser().getId();
        this.userLoginName = session.getUser().getLoginName();
        this.accessToken = session.getAccessToken();
        this.authorities = session.getUser().getAuthorities();
        this.details = new UserAuthenticationDetails(session, request);
    }

    @Override
    public Integer getPrincipal() {
        return userId;
    }

    @Override
    public String getName() {
        return userLoginName;
    }

    @Override
    public String getCredentials() {
        return accessToken;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public UserAuthenticationDetails getDetails() {
        return details;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}

    @Override
    public boolean implies(Subject subject) {
        return Authentication.super.implies(subject);
    }

}
