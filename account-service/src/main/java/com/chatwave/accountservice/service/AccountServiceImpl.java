package com.chatwave.accountservice.service;

import com.chatwave.accountservice.client.AuthClient;
import com.chatwave.accountservice.client.dto.*;
import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.dto.PatchAccountRequest;
import com.chatwave.accountservice.repository.AccountRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository repository;
    private final AuthClient authClient;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TokenSet createAccount(Account account, String loginName, String password) {
        if(repository.findByDisplayName(account.getDisplayName()).isPresent())
            throw new ResponseStatusException(CONFLICT, "Account with given displayName already exists");

        try {
            var response = authClient.createUser(new RegisterRequest(loginName, password));
            account.setId(response.userId());
        } catch (FeignException.FeignClientException.Conflict e) {
            throw new ResponseStatusException(CONFLICT, "Account with given loginName already exists.");
        }
        repository.save(account);
        return authClient.createSessions(new CreateSessionRequest(account.getId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TokenSet authenticateAccount(AuthenticationRequest authentication) {
        try {
            var userId = authClient
                    .authenticateUser(authentication)
                    .userId();
            return authClient.createSessions(new CreateSessionRequest(userId));
        } catch (FeignException.FeignClientException.Unauthorized e) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid login or password");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account getAccountById(Integer accountId) {
        return repository.findById(accountId)
                .orElseThrow( () ->
                        new ResponseStatusException(NOT_FOUND, "User with given id does not exist.")
                );
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void patchAccount(Integer accountId, PatchAccountRequest patchAccountRequest) {
        var displayName = patchAccountRequest.displayName();
        if(displayName != null) {
            var account = repository.findById(accountId)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Account with given id does not exist."));

            account.setDisplayName(displayName);
            repository.save(account);
        }

        if(patchAccountRequest.newPassword() != null)
            authClient.patchUser(accountId, new PatchUserRequest(patchAccountRequest.newPassword()));
    }
}
