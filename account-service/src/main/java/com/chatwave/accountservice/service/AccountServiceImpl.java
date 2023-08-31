package com.chatwave.accountservice.service;

import com.chatwave.accountservice.client.AuthService;
import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.accountservice.domain.dto.CreateUserRequest;
import com.chatwave.accountservice.domain.dto.PatchPasswordRequest;
import com.chatwave.accountservice.domain.dto.TokenSet;
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
    private final AuthService authService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TokenSet createAccount(Account account, String password) {
        var optional = repository.findByLoginOrDisplayName(account.getLoginName(), account.getDisplayName());

        if(optional.isPresent()) {
            var found = optional.get();
            if(found.getLoginName().equals( account.getLoginName() ))
                throw new ResponseStatusException(CONFLICT, "Account with given loginName already exists.");
            else
                throw new ResponseStatusException(CONFLICT, "Account with given displayName already exists.");
        }

        repository.save(account);

        var createUserRequest = new CreateUserRequest(account.getId(), password);
        try {
            return authService.createUser(createUserRequest);
        } catch(FeignException.Conflict e) {
            log.warn("Inconsistency in data. Conflict when tried to create user with id " + account.getId());
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TokenSet authenticateAccount(String loginName, String password) {
        var account = repository.findByLoginName(loginName)
                .orElseThrow( () ->
                        new ResponseStatusException(BAD_REQUEST, "Invalid loginName or password.")
                );

        var authenticateUserRequest = new AuthenticateUserRequest(account.getId(), password);
        return authService.authenticateUser(authenticateUserRequest);
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
    @Override
    public Boolean doesAccountExist(Integer accountId) {
        return repository.findById(accountId).isPresent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account getAccountByDisplayName(String displayName) {
        return repository.findByDisplayName(displayName)
                .orElseThrow(() ->
                        new ResponseStatusException(NOT_FOUND, "User with given displayName does not exist.")
                        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void patchAccountPassword(Integer accountId, PatchPasswordRequest patchPasswordRequest) {
        authService.patchUserPassword(accountId, patchPasswordRequest);
    }
}
