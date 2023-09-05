package com.chatwave.authservice.controller;

import com.chatwave.authservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.authservice.domain.dto.CreateUserRequest;
import com.chatwave.authservice.domain.dto.PatchUserRequest;
import com.chatwave.authservice.domain.dto.TokenSetResponse;
import com.chatwave.authservice.domain.session.SessionMapper;
import com.chatwave.authservice.domain.user.UserAuthentication;
import com.chatwave.authservice.domain.user.UserMapper;
import com.chatwave.authservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users", produces = APPLICATION_JSON) // todo: handle 406 all where
@PreAuthorize("hasAuthority('SCOPE_server')")
public class UserController {
    private final UserService service;
    private final UserMapper mapper;
    private final SessionMapper sessionMapper;

    @GetMapping("/authentication")
    public UserAuthentication getUserAuthentication(HttpServletRequest request) {
        return service.getUserAuthentication(request);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public TokenSetResponse createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        var user = mapper.toUser(createUserRequest);
        var session = service.createUser(user);
        return sessionMapper.toTokenSetResponse(session);
    }

    @PostMapping("/authenticate")
    public TokenSetResponse authenticateUser(@Valid @RequestBody AuthenticateUserRequest authenticateUserRequest) {
        var user = mapper.toUser(authenticateUserRequest);
        var session = service.authenticateUser(user);
        return sessionMapper.toTokenSetResponse(session);
    }

    @PatchMapping("/{userId}")
    public void patchUser(@PathVariable Integer userId, @Valid @RequestBody PatchUserRequest patchUser) {
        var user = mapper.toUser(userId, patchUser);
        var newPassword = patchUser.newPassword();
        service.patchUser(user, newPassword);
    }
}
