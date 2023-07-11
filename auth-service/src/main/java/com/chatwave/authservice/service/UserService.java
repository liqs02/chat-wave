package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.domain.dto.UserTokenSet;

public interface UserService {
    /**
     * Creates new user if id is not occupied.
     * Create refresh token.
     *
     * @param user
     * @return refresh and access token
     * */
    UserTokenSet create(User user);
}
