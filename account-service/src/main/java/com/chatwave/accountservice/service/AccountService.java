package com.chatwave.accountservice.service;

import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.dto.TokenSetResponse;

public interface AccountService {
    /**
     * Creates an account.
     * Creates a user in auth-service.
     *
     * @param account
     * @return access and refresh token from auth-service.
     */
    TokenSetResponse createAccount(Account account);

    /**
     * Searches user with given loginName.
     * Authenticates user in auth-service.
     *
     * @param account with loginName and password
     * @return access and refresh token from auth-service.
     */
    TokenSetResponse authenticateAccount(Account account);
}
