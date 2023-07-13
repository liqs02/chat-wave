package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.Session;
import com.chatwave.authservice.domain.User;

public interface UserService {
    /**
     * Creates a new user.
     * Creates session for user.
     *
     * @param user
     * @return new session
     */
    Session createUser(User user);

    /**
     * Authenticates a user.
     * Creates session for user.
     *
     * @param user
     * @return new session
     */
    Session authenticateUser(User user);
}
