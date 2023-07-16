package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.domain.session.Session;

public interface UserService {
    /**
     * Creates a new user by provided id and password.
     * Creates session for user.
     *
     * @param user
     * @return new session
     */
    Session createUser(User user);

    /**
     * Authenticates a user by id and password.
     * Downloads user from database.
     * Creates session for user.
     *
     * @param user
     * @return new session
     */
    Session authenticateUser(User user);

}
