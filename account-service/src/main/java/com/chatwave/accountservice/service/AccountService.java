package com.chatwave.accountservice.service;

import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.dto.TokenSetResponse;

public interface AccountService {
    /**
     * Creates an account.
     * Creates a user in auth-service.
     *
     * @param createAccountRequest
     * @return access and refresh token from auth-service.
     */
    TokenSetResponse createAccount(Account account, String password);

    /**
     * Searches user with given loginName.
     * Authenticates user in auth-service.
     *
     * @param authenticateUserRequest
     * @return access and refresh token from auth-service.
     */
    TokenSetResponse authenticateAccount(String loginName, String password);
}
