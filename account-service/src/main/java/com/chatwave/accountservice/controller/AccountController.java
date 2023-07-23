package com.chatwave.accountservice.controller;

import com.chatwave.accountservice.domain.AccountMapper;
import com.chatwave.accountservice.domain.dto.*;
import com.chatwave.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public TokenSet createAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest) {
        var account = mapper.toAccount(createAccountRequest);
        return service.createAccount(account, createAccountRequest.password());
    }

    @PostMapping("/authenticate")
    public TokenSet authenticateAccount(@Valid @RequestBody AuthenticateAccountRequest authenticateAccountRequest) {
        return service.authenticateAccount(authenticateAccountRequest.loginName(), authenticateAccountRequest.password());
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("#accountId == authentication.principal")
    public AccountDetails getAccountDetails(@PathVariable Integer accountId) {
        var account = service.getAccount(accountId);
        return mapper.toAccountDetails(account);
    }

    @GetMapping("/{id}/showcase")
    public AccountShowcase getAccountShowcase(@PathVariable Integer id) {
        var account = service.getAccount(id);
        return mapper.toAccountShowcase(account);
    }
}
