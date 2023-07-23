package com.chatwave.accountservice.service;

import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.dto.TokenSet;

public interface AccountService {
    /**
     * Creates an account.
     * Creates a user in auth-service.
     *
     * @param createAccountRequest
     * @return access and refresh token from auth-service.
     */
    TokenSet createAccount(Account account, String password);

    /**
     * Searches account with given loginName.
     * Authenticates user in auth-service.
     *
     * @param authenticateUserRequest
     * @return access and refresh token from auth-service.
     */
    TokenSet authenticateAccount(String loginName, String password);

    /**
     * Searches an account with given id.
     *
     * @param accountId
     * @return account
     */
    Account getAccountById(Integer accountId);

    /**
     * Searches for an account with a matching displayName.
     *
     * @param displayName
     * @return account
     */
    Account getAccountByDisplayName(String displayName);
}
