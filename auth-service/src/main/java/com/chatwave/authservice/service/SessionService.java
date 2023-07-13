package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.Session;
import com.chatwave.authservice.domain.User;

import java.util.Optional;

public interface SessionService {
    /**
     * Creates session for user.
     *
     * @param user
     * @return new session
     * */
    Session createSession(User user);

    /**
     * Gets sessions by accessToken.
     * If session is expired, throws exception.
     *
     * @param accessToken
     * @return session
     */
    Optional<Session> getActiveSession(String accessToken);

    /**
     * Searches session by refresh token.
     * Creates new refresh and access tokens.
     *
     * @param refreshToken refresh token id
     * @return new refresh token
     */
    Session refreshSession(String refreshToken);

    /**
     * Deactivates session.
     * 
     * @param sessionId
     */
    void deactivateSession(Long sessionId);
}
