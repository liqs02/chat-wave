package com.chatwave.accountservice.service;

import com.chatwave.accountservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.accountservice.domain.dto.CreateAccountRequest;
import com.chatwave.accountservice.domain.dto.TokenSetResponse;

public interface AccountService {
    /**
     * Creates an account.
     * Creates a user in auth-service.
     *
     * @param createAccountRequest
     * @return access and refresh token from auth-service.
     */
    TokenSetResponse createAccount(CreateAccountRequest createAccountRequest);

    /**
     * Searches user with given loginName.
     * Authenticates user in auth-service.
     *
     * @param authenticateUserRequest
     * @return access and refresh token from auth-service.
     */
    TokenSetResponse authenticateAccount(AuthenticateUserRequest authenticateUserRequest);
}
