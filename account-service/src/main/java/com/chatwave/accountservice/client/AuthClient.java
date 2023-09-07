package com.chatwave.accountservice.client;

import com.chatwave.accountservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.accountservice.domain.dto.CreateUserRequest;
import com.chatwave.accountservice.domain.dto.PatchUserRequest;
import com.chatwave.accountservice.domain.dto.TokenSet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@FeignClient("auth-service")
public interface AuthClient extends com.chatwave.authclient.client.AuthClient {
    @PostMapping(value = "/users", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    TokenSet createUser(CreateUserRequest createUserRequest);

    @PostMapping(value = "/users/authenticate", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    TokenSet authenticateUser(AuthenticateUserRequest authenticateUserRequest);

    @PatchMapping(value = "/users/{userId}", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    void patchUser(@PathVariable Integer userId, PatchUserRequest patchUserRequest);
}
