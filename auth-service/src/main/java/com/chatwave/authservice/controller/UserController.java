package com.chatwave.authservice.controller;

import com.chatwave.authservice.domain.dto.request.AuthenticationRequest;
import com.chatwave.authservice.domain.dto.request.UpdatePasswordRequest;
import com.chatwave.authservice.domain.dto.request.RegisterRequest;
import com.chatwave.authservice.domain.dto.response.AuthenticationResponse;
import com.chatwave.authservice.domain.dto.response.RegisterResponse;
import com.chatwave.authservice.domain.user.UserMapper;
import com.chatwave.authservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
@PreAuthorize("hasAuthority('SCOPE_server')")
public class UserController {
    private final UserService service;
    private final UserMapper mapper;

    @PostMapping
    @ResponseStatus(CREATED)
    public RegisterResponse createUser(@Valid @RequestBody RegisterRequest body) {
        var data = mapper.toUser(body);
        var user = service.createUser(data);
        return mapper.toCreateUserResponse(user);
    }

    @PostMapping("/authenticate")
    public AuthenticationResponse authenticateUser(@Valid @RequestBody AuthenticationRequest body) {
        var authData = mapper.toUser(body);
        var user = service.authenticateUser(authData);
        return mapper.toAuthenticateUserResponse(user);
    }

    @PutMapping("/{userId}/password")
    public void updateUserPassword(@PathVariable Integer userId, @Valid @RequestBody UpdatePasswordRequest body) {
        service.updateUserPassword(userId, body.newPassword());
    }
}
