package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

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

    /**
     * Authenticates a user by id and password.
     * Changes user's password.
     *
     * @param user with id and old password
     * @param newPassword
     */
    void patchUserPassword(User user, String newPassword);

    /**
     * Searches session by accessToken.
     * Creates UserAuthentication.
     *
     * @param request with User-Authorization header with accessToken
     * @return user's authentication information
     */
    Authentication getAuthentication(HttpServletRequest request);
}
