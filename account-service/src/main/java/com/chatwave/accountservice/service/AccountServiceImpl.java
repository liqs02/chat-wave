package com.chatwave.accountservice.service;

import com.chatwave.accountservice.client.AuthService;
import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.dto.CreateUserRequest;
import com.chatwave.accountservice.domain.dto.TokenSetResponse;
import com.chatwave.accountservice.repository.AccountRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@Setter(onMethod_=@Autowired)
@Slf4j
public class AccountServiceImpl implements AccountService {
    AccountRepository repository;
    AuthService authService;

    @Override
    @Transactional
    public TokenSetResponse createAccount(Account account, String password) {
        var optional = repository.findByLoginName(account.getLoginName());
        if(optional.isPresent())
            throw new ResponseStatusException(CONFLICT, "Account with given loginName already exists.");
        repository.save(account);

        var createUserRequest = new CreateUserRequest(account.getId(), password);
        try {
            return authService.createUser(createUserRequest);
        } catch(FeignException.FeignServerException.Conflict conflict) {
            log.warn("Inconsistency in data. Conflict when tried to create user with id " + account.getId());
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public TokenSetResponse authenticateAccount(String loginName, String password) {
        return null;
    }
}
