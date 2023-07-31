package com.chatwave.accountservice.client;

import com.chatwave.accountservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.accountservice.domain.dto.CreateUserRequest;
import com.chatwave.accountservice.domain.dto.PatchPasswordRequest;
import com.chatwave.accountservice.domain.dto.TokenSet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("auth-service")
public interface AuthService extends com.chatwave.authclient.client.AuthService {
    @PostMapping(value = "/users", consumes = "application/json")
    TokenSet createUser(CreateUserRequest createUserRequest);

    @PostMapping(value = "/users/authenticate", consumes = "application/json")
    TokenSet authenticateUser(AuthenticateUserRequest authenticateUserRequest);

    @PatchMapping(value = "/users/{id}/password")
    void patchUserPassword(@PathVariable Integer id, PatchPasswordRequest patchPasswordRequest);
}
