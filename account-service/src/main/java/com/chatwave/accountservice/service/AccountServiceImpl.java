package com.chatwave.accountservice.service;

import com.chatwave.accountservice.client.AuthService;
import com.chatwave.accountservice.domain.AccountMapper;
import com.chatwave.accountservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.accountservice.domain.dto.CreateAccountRequest;
import com.chatwave.accountservice.domain.dto.CreateUserRequest;
import com.chatwave.accountservice.domain.dto.TokenSetResponse;
import com.chatwave.accountservice.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static lombok.AccessLevel.NONE;
import static org.springframework.http.HttpStatus.CONFLICT;

@Service
@Setter(onMethod_=@Autowired)
public class AccountServiceImpl implements AccountService {
    AccountRepository repository;
    AccountMapper mapper;
    @Setter(NONE)
    AuthService authService;

    @Override
    @Transactional
    public TokenSetResponse createAccount(CreateAccountRequest createAccountRequest) {
        var account = mapper.toAccount(createAccountRequest);
        var optional = repository.findByLoginName(createAccountRequest.loginName());
        if(optional.isPresent())
            throw new ResponseStatusException(CONFLICT, "Account with given loginName already exists.");
        repository.save(account);

        var createUserRequest = new CreateUserRequest(account.getId(), createAccountRequest.password());
        return authService.createUser(createUserRequest);
    }

    @Override
    public TokenSetResponse authenticateAccount(AuthenticateUserRequest authenticateUserRequest) {
        return null;
    }
}
