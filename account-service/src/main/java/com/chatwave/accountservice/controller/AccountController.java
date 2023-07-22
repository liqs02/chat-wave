package com.chatwave.accountservice.controller;

import com.chatwave.accountservice.domain.AccountMapper;
import com.chatwave.accountservice.domain.dto.CreateAccountRequest;
import com.chatwave.accountservice.domain.dto.TokenSetResponse;
import com.chatwave.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/accounts")
@Setter(onMethod_=@Autowired)
public class AccountController {
    AccountService service;
    AccountMapper mapper;

    @PostMapping
    @ResponseStatus(CREATED)
    public TokenSetResponse createAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest) { // TODO: add validation exception message to response (in auth-service too)!
        var account = mapper.toAccount(createAccountRequest);
        return service.createAccount(account, createAccountRequest.password());
    }
}
