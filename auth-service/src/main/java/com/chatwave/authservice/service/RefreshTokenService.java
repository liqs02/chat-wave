package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.RefreshToken;
import com.chatwave.authservice.domain.User;

import java.util.UUID;

public interface RefreshTokenService {
    /**
     * Creates refresh token for user.
     *
     * @param user
     * @return refresh token
     * */
    RefreshToken create(User user);

    /**
     * Checks that refresh token is older than 2 minutes
     * Deletes provided refresh token.
     * Creates new refresh token.
     *
     * @param tokenId refresh token id
     * @return new refresh token
     */
    RefreshToken refresh(UUID tokenId);

    /**
     * Deletes provided refresh token.
     *
     * @param tokenId refresh token id to invalidate
     */
    void invalidate(UUID tokenId);
}
