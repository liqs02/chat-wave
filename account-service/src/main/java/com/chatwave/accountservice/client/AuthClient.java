package com.chatwave.accountservice.client;

import com.chatwave.accountservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.accountservice.domain.dto.CreateUserRequest;
import com.chatwave.accountservice.domain.dto.PatchPasswordRequest;
import com.chatwave.accountservice.domain.dto.TokenSet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON_PATCH_JSON;

@FeignClient("auth-service")
public interface AuthClient extends com.chatwave.authclient.client.AuthClient {
    @PostMapping(value = "/users")
    TokenSet createUser(CreateUserRequest createUserRequest);

    @PostMapping(value = "/users/authenticate")
    TokenSet authenticateUser(AuthenticateUserRequest authenticateUserRequest);

    @PatchMapping(value = "/users/{id}/password", produces = APPLICATION_JSON_PATCH_JSON)
    void patchUserPassword(@PathVariable Integer id, PatchPasswordRequest patchPasswordRequest);
}
