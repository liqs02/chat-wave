package com.chatwave.authservice.controller;

import com.chatwave.authservice.domain.dto.RefreshSessionRequest;
import com.chatwave.authservice.domain.dto.SessionResponse;
import com.chatwave.authservice.domain.dto.TokenSetResponse;
import com.chatwave.authservice.domain.session.SessionMapper;
import com.chatwave.authservice.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class SessionController {
    private final SessionService service;
    private final SessionMapper mapper;

    @PostMapping("/sessions/refresh")
    public TokenSetResponse refreshTokens(@Valid @RequestBody RefreshSessionRequest refreshSessionRequest) {
        var session = service.refreshSession(refreshSessionRequest.refreshToken());
        return mapper.toTokenSetResponse(session);
    }

    @GetMapping("/{userId}/sessions")
    @PreAuthorize("#userId == authentication.principal")
    public List<SessionResponse> getActiveSessionsByUserId(@PathVariable Integer userId) {
        var sessionList = service.getActiveSessionsByUserId(userId);
        return sessionList
                .stream()
                .map(mapper::toSessionResponse)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{userId}/sessions")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("#userId == authentication.principal")
    public void expireUserSessions(@PathVariable Integer userId) {
        service.expireUserSessions(userId);
    }

    @DeleteMapping("/{userId}/sessions/{sessionId}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("#userId == authentication.principal")
    public void expireSession(@PathVariable Integer userId, @PathVariable Long sessionId) {
        service.expireSession(userId, sessionId);
    }
}
