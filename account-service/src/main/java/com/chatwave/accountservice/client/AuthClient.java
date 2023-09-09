package com.chatwave.accountservice.client;

import com.chatwave.accountservice.client.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@FeignClient("auth-service")
public interface AuthClient extends com.chatwave.authclient.client.AuthClient {
    @PostMapping(value = "/users", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    RegisterResponse createUser(RegisterRequest registerRequest); // todo @RequestParam check it

    @PostMapping(value = "/users/authenticate", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    AuthenticationResponse authenticateUser(AuthenticationRequest authenticationRequest);

    @PostMapping(value = "/sessions", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    TokenSet createSessions(CreateSessionRequest createSessionRequest);

    @PutMapping(value = "/users/{userId}/password", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    void patchUser(@PathVariable Integer userId, PatchUserRequest patchUserRequest);
}
