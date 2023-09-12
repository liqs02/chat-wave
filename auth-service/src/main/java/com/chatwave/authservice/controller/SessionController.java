package com.chatwave.authservice.controller;

import com.chatwave.authservice.domain.dto.request.CreateSessionRequest;
import com.chatwave.authservice.domain.dto.request.RefreshSessionRequest;
import com.chatwave.authservice.domain.dto.response.GetSessionResponse;
import com.chatwave.authservice.domain.dto.response.TokenSetResponse;
import com.chatwave.authservice.domain.session.SessionMapper;
import com.chatwave.authservice.domain.user.UserAuthentication;
import com.chatwave.authservice.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/sessions", produces = APPLICATION_JSON)
public class SessionController {
    private final SessionService service;
    private final SessionMapper mapper;

    @GetMapping("/authentication")
    public UserAuthentication getUserAuthentication(HttpServletRequest request) {
        return service.getAuthentication(request);
    }

    @GetMapping
    public List<GetSessionResponse> getActiveSessionsByUserId(@AuthenticationPrincipal Integer userId) {
        return service.getNotExpiredSessionsByUserId(userId)
                .parallelStream()
                .map(mapper::toSessionResponse)
                .toList();
    }

    @PostMapping(consumes = APPLICATION_JSON)
    @PreAuthorize("hasAuthority('SCOPE_server')")
    public TokenSetResponse createSessions(@Valid @RequestBody CreateSessionRequest body) {
        var session = service.createSession(body.userId());
        return mapper.toTokenSetResponse(session);
    }

    @PostMapping(value = "/refresh", consumes = APPLICATION_JSON)
    public TokenSetResponse refreshTokens(@Valid @RequestBody RefreshSessionRequest body) {
        var session = service.refreshSession(body.refreshToken());
        return mapper.toTokenSetResponse(session);
    }

    @DeleteMapping
    public void expireUserSessions(@AuthenticationPrincipal Integer userId) {
        service.expireSessionsByUserId(userId);
    }

    @DeleteMapping("/{sessionId}")
    public void expireSession(@PathVariable Long sessionId, @AuthenticationPrincipal Integer userId) {
        service.expireSession(sessionId, userId);
    }
}
