package com.chatwave.accountservice.controller;

import com.chatwave.accountservice.domain.AccountMapper;
import com.chatwave.accountservice.domain.dto.*;
import com.chatwave.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping(value = "/accounts", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
@RequiredArgsConstructor
public class AccountController {
    private final AccountService service;
    private final AccountMapper mapper;

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

    @GetMapping("/{accountId}/exist")
    @PreAuthorize("hasAuthority('SCOPE_server')") // todo: add integration tests
    public void doesAccountExist(@PathVariable Integer accountId) {
        var doesExist = service.doesAccountExist(accountId);
        if(!doesExist) throw new ResponseStatusException(NOT_FOUND, "User with given id does not exist");
    }

    @GetMapping("/{accountId}/showcase")
    public AccountShowcase getAccountShowcase(@PathVariable Integer accountId) {
        var account = service.getAccountById(accountId);
        return mapper.toAccountShowcase(account);
    }

    @PatchMapping("/{accountId}")
    @PreAuthorize("#accountId == authentication.principal")
    public void patchAccount(@PathVariable Integer accountId, @Valid @RequestBody PatchAccountRequest patchAccountRequest) {
        service.patchAccount(accountId, patchAccountRequest);
    }
}
