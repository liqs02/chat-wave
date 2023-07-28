package com.chatwave.authservice.controller;

import com.chatwave.authservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.authservice.domain.dto.CreateUserRequest;
import com.chatwave.authservice.domain.dto.PatchPasswordRequest;
import com.chatwave.authservice.domain.dto.TokenSetResponse;
import com.chatwave.authservice.domain.session.SessionMapper;
import com.chatwave.authservice.domain.user.UserMapper;
import com.chatwave.authservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@Setter(onMethod_=@Autowired)
@RequestMapping("/users")
@PreAuthorize("hasAuthority('SCOPE_server')")
public class UserController {
    private UserService service;
    private UserMapper mapper;
    private SessionMapper sessionMapper;

    @GetMapping("/authentication")
    public Authentication getUserAuthentication(HttpServletRequest request) {
        return service.getUserAuthentication(request);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public TokenSetResponse createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        var user = mapper.toUser(createUserRequest);
        var session = service.createUser(user);
        return sessionMapper.toTokenSetResponse(session);
    }

    @PostMapping("/auth")
    public TokenSetResponse authenticateUser(@Valid @RequestBody AuthenticateUserRequest authenticateUserRequest) {
        var user = mapper.toUser(authenticateUserRequest);
        var session = service.authenticateUser(user);
        return sessionMapper.toTokenSetResponse(session);
    }

    @PatchMapping("/{id}/password")
    public void patchUserPassword(@PathVariable Integer id, @Valid @RequestBody PatchPasswordRequest patchPasswordRequest) {
        var user = mapper.toUser(id, patchPasswordRequest);
        var newPassword = patchPasswordRequest.newPassword();
        service.patchUserPassword(user, newPassword);
    }
}
