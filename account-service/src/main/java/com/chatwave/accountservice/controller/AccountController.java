package com.chatwave.accountservice.controller;

import com.chatwave.accountservice.client.dto.AuthenticationRequest;
import com.chatwave.accountservice.client.dto.TokenSet;
import com.chatwave.accountservice.domain.AccountMapper;
import com.chatwave.accountservice.domain.dto.AccountResponse;
import com.chatwave.accountservice.domain.dto.CreateAccountRequest;
import com.chatwave.accountservice.domain.dto.PatchAccountRequest;
import com.chatwave.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(value = "/accounts", produces = APPLICATION_JSON)
@RequiredArgsConstructor
public class AccountController {
    private final AccountService service;
    private final AccountMapper mapper;

    @PostMapping(consumes = APPLICATION_JSON)
    @ResponseStatus(CREATED)
    public TokenSet createAccount(@Valid @RequestBody CreateAccountRequest body) {
        var account = mapper.toAccount(body);
        return service.createAccount(account, body.loginName(), body.password());
    }

    @PostMapping(value = "/authenticate", consumes = APPLICATION_JSON)
    public TokenSet authenticateAccount(@RequestBody AuthenticationRequest body) {
        return service.authenticateAccount(body);
    }

    @GetMapping("/{accountId}/exist")
    @PreAuthorize("hasAuthority('SCOPE_server')")
    public void doesAccountExist(@PathVariable Integer accountId) {
        service.getAccountById(accountId);
    }

    @GetMapping("/{accountId}/showcase")
    public AccountResponse getAccountShowcase(@PathVariable Integer accountId) {
        var account = service.getAccountById(accountId);
        return mapper.toAccountShowcase(account);
    }

    @PatchMapping(value = "/{accountId}", consumes = APPLICATION_JSON)
    @PreAuthorize("#accountId == authentication.principal")
    public void patchAccount(@PathVariable Integer accountId, @Valid @RequestBody PatchAccountRequest body) {
        service.patchAccount(accountId, body);
    }
}
