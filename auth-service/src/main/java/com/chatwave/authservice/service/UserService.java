package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.dto.PatchUserRequest;
import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.domain.user.UserAuthentication;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    /**
     * Gets accessToken from User-Authorization header.
     * Searches session by accessToken.
     * Creates UserAuthentication.
     *
     * @param request with User-Authorization header with accessToken
     * @return user's authentication
     */
    UserAuthentication getUserAuthentication(HttpServletRequest request);

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
     * Patches user.
     *
     * @param user with data to authenticate (id and password)
     * @param newPassword to update a user
     */
    void patchUser(User user, String newPassword);
}
