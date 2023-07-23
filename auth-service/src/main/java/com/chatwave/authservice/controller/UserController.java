package com.chatwave.authservice.controller;

import com.chatwave.authservice.domain.UserMapper;
import com.chatwave.authservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.authservice.domain.dto.CreateUserRequest;
import com.chatwave.authservice.domain.dto.TokenSetResponse;
import com.chatwave.authservice.domain.session.SessionMapper;
import com.chatwave.authservice.service.UserService;
import jakarta.validation.Valid;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@Setter(onMethod_=@Autowired)
@RequestMapping("/users")
@PreAuthorize("hasAuthority('SCOPE_server')")
public class UserController {
    UserService service;
    UserMapper mapper;
    SessionMapper sessionMapper;

    @PostMapping
    @ResponseStatus(CREATED)
    public TokenSetResponse createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        final var user = mapper.toUser(createUserRequest);
        var session = service.createUser(user);
        return sessionMapper.toTokenSetResponse(session);
    }

    @PostMapping("/authenticate")
    public TokenSetResponse authenticateUser(@Valid @RequestBody AuthenticateUserRequest authenticateUserRequest) {
        final var user = mapper.toUser(authenticateUserRequest);
        var session = service.authenticateUser(user);
        return sessionMapper.toTokenSetResponse(session);
    }
}
