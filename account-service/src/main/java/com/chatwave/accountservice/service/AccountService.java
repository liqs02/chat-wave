package com.chatwave.accountservice.service;

import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.dto.PatchPasswordRequest;
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
     * Checks that account with given id does exist.
     *
     * @param accountId
     * @return boolean represents that account exists
     */
    Boolean doesAccountExist(Integer accountId);

    /**
     * Changes password of user with given id.
     *
     * @param accountId
     * @param patchPasswordRequest
     */
    void patchAccountPassword(Integer accountId, PatchPasswordRequest patchPasswordRequest);
}
