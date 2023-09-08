package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.user.User;

public interface UserService {
    /**
     * Encodes password.
     * Saves user in database.
     *
     * @param user with completed loginName and password
     * @return user
     */
    User createUser(User user);

    /**
     * Authenticates a user by login and password.
     *
     * @param user completed loginName and password
     * @return found user
     */
    User authenticateUser(User user);

    /**
     * Patches user's password.
     *
     * @param userId
     * @param newPassword
     */
    void updateUserPassword(Integer userId, String newPassword);
}
