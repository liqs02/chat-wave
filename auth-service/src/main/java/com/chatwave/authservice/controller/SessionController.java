package com.chatwave.authservice.controller;

import com.chatwave.authservice.domain.dto.RefreshSessionRequest;
import com.chatwave.authservice.domain.dto.SessionResponse;
import com.chatwave.authservice.domain.dto.TokenSetResponse;
import com.chatwave.authservice.domain.session.SessionMapper;
import com.chatwave.authservice.service.SessionService;
import jakarta.validation.Valid;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@Setter(onMethod_=@Autowired)
@RequestMapping("/users")
public class SessionController {
    private SessionService service;
    private SessionMapper mapper;

    @PostMapping("/sessions/refresh")
    public TokenSetResponse refreshTokens(@Valid @RequestBody RefreshSessionRequest refreshSessionRequest) {
        var session = service.refreshSession(refreshSessionRequest.refreshToken());
        return mapper.toTokenSetResponse(session);
    }

    @GetMapping("/{userId}/sessions")
    @PreAuthorize("#userId == authentication.principal")
    public List<SessionResponse> getUserCurrentSessions(@PathVariable Integer userId) {
        var sessionList = service.getUserCurrentSessions(userId);
        return sessionList
                .stream()
                .map(mapper::toSessionResponse)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{userId}/sessions")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("#userId == authentication.principal")
    public void expireAllUserSessions(@PathVariable Integer userId) {
        service.expireAllUserSessions(userId);
    }

    @DeleteMapping("/{userId}/sessions/{sessionId}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("#userId == authentication.principal")
    public void expireUserSession(@PathVariable Integer userId, @PathVariable Long sessionId) {
        service.expireUserSession(userId, sessionId);
    }
}
