package com.chatwave.authclient.domain;

import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.List;

/**
 * Principal is represented by the userId.
 * Credentials are represented by provided accessToken.
 */
@Setter
public class UserAuthentication implements Authentication {
    private Integer userId;
    private String accessToken;
    private List<GrantedAuthority> authorities;
    private UserAuthenticationDetails details;

    public UserAuthentication() {
        authorities = List.of();
        details = new UserAuthenticationDetails();
    }

    @Override
    public Integer getPrincipal() {
        return userId;
    }

    @Override
    public String getName() {
        return userId.toString();
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

    public void setPrincipal(Integer userId) {
        this.userId = userId;
    }

    public void setCredentials(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setDetails(UserAuthenticationDetails details) {
        this.details = details;
    }

    @Override
    public boolean implies(Subject subject) {
        return Authentication.super.implies(subject);
    }
}
