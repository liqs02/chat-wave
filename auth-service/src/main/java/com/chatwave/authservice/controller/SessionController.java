package com.chatwave.authservice.controller;

import com.chatwave.authservice.domain.dto.RefreshSessionRequest;
import com.chatwave.authservice.domain.dto.SessionResponse;
import com.chatwave.authservice.domain.dto.TokenSetResponse;
import com.chatwave.authservice.domain.session.SessionMapper;
import com.chatwave.authservice.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/sessions", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
public class SessionController {
    private final SessionService service;
    private final SessionMapper mapper;

    @GetMapping
    public List<SessionResponse> getActiveSessionsByUserId(@AuthenticationPrincipal Integer userId) {
        return service.getNotExpiredSessionsByUserId(userId)
                .parallelStream()
                .map(mapper::toSessionResponse)
                .toList();
    }

    @PostMapping("/refresh")
    public TokenSetResponse refreshTokens(@Valid @RequestBody RefreshSessionRequest refreshSessionRequest) {
        var session = service.refreshSession(refreshSessionRequest.refreshToken());
        return mapper.toTokenSetResponse(session);
    }

    @DeleteMapping
    public void expireUserSessions(@AuthenticationPrincipal Integer userId) {
        service.expireSessionsByUserId(userId);
    }

    @DeleteMapping("/{sessionId}")
    public void expireSession(@AuthenticationPrincipal Integer userId, @PathVariable Long sessionId) {
        service.expireSession(sessionId, userId);
    }
}
