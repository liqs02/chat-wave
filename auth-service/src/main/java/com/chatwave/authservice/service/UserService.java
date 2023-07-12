package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.domain.dto.UserTokenSet;

import java.util.UUID;

public interface UserService {
    /**
     * Creates new user if id is not occupied.
     * Creates tokens.
     *
     * @param user
     * @return refresh and access token
     * */
    UserTokenSet create(User user);

    /**
     * Authenticates a user.
     * Creates tokens.
     *
     * @param user
     * @return refresh and access token
     * */
    UserTokenSet authenticate(User user);


    /**
     * Checks provided refresh token.
     * Creates new access token.
     * Create new refresh token.
     *
     * @param refreshTokenId
     * @return new refresh and access token
     */
    UserTokenSet refreshToken(UUID refreshTokenId);
}
