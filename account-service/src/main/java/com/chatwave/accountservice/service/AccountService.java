package com.chatwave.accountservice.service;

import com.chatwave.accountservice.client.dto.AuthenticationRequest;
import com.chatwave.accountservice.client.dto.TokenSet;
import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.dto.PatchAccountRequest;

public interface AccountService {
    /**
     * Creates an account.
     * Creates a user in auth-service.
     * Creates session in auth-service.
     *
     * @param account
     * @param loginName
     * @param password
     * @return accessToken and refreshToken
     */
    TokenSet createAccount(Account account, String loginName, String password);

    /**
     * Authenticates user in auth-service.
     * Creates session in auth-service.
     *
     * @param authenticationRequest
     * @return access and refresh token from auth-service.
     */
    TokenSet authenticateAccount(AuthenticationRequest authenticationRequest);

    /**
     * Searches an account with given id.
     *
     * @param accountId
     * @return account
     */
    Account getAccountById(Integer accountId);

    /**
     * Patches displayName if is provided.
     * Patches password if is provided.
     *
     * @param accountId
     * @param patchAccountRequest
     */
    void patchAccount(Integer accountId, PatchAccountRequest patchAccountRequest);
}
