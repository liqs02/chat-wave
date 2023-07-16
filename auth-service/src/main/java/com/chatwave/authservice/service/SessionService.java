package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.domain.session.Session;

import java.util.List;

public interface SessionService {
    /**
     * Creates session for user.
     *
     * @param user
     * @return new session
     * */
    Session createSession(User user);

    /**
     * Searches session by refresh token.
     * Creates new refresh and access tokens.
     *
     * @param refreshToken refresh token id
     * @return new refresh token
     */
    Session refreshSession(String refreshToken);

    /**
     * Gets all not expired user's sessions.
     *
     * @param userId
     * @return user's sessions
     */
    List<Session> getUserCurrentSessions(Integer userId);

    /**
     * Finds not expired session of specified users.
     * Expires these sessions.
     *
     * @param userId
     */
    void expireAllUserSessions(Integer userId);

    /**
     * Expires session of specified user.
     *
     * @param sessionId
     * @param userId check value used to ensure that the session is owned by the user
     */
    void expireUserSession(Integer userId, Long sessionId);
}