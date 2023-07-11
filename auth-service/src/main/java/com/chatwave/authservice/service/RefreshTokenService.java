package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.RefreshToken;
import com.chatwave.authservice.domain.User;

public interface RefreshTokenService {
    /**
     * Creates new registered refresh token for user.
     *
     * @param user
     * @return refresh token
     * */
    RefreshToken create(User user);
}
