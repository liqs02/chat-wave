package com.chatwave.authservice.controller;

import com.chatwave.authservice.domain.dto.RefreshSessionRequest;
import com.chatwave.authservice.domain.dto.SessionResponse;
import com.chatwave.authservice.domain.dto.TokenSetResponse;
import com.chatwave.authservice.domain.session.SessionMapper;
import com.chatwave.authservice.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang.Validate.notNull;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasAuthority('SCOPE_ui')")
public class SessionController {
    private SessionService service;
    private SessionMapper mapper;

    @PostMapping("/sessions/refresh")
    public TokenSetResponse refreshTokens(@Valid @RequestBody RefreshSessionRequest refreshSessionRequest) {
        var session = service.refreshSession(refreshSessionRequest.refreshToken());
        return mapper.toTokenSetResponse(session);
    }

    @GetMapping("/{userId}/sessions")
    @PreAuthorize("#principalId == #userId")
    public List<SessionResponse> getUserCurrentSessions(@AuthenticationPrincipal Integer principalId, @PathVariable Integer userId) {
        var sessionList = service.getUserCurrentSessions(userId);
        return sessionList
                .stream()
                .map(mapper::toSessionResponse)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{userId}/sessions")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("#principalId == #userId")
    public void expireAllUserSessions(@AuthenticationPrincipal Integer principalId, @PathVariable Integer userId) {
        service.expireAllUserSessions(userId);
    }

    @DeleteMapping("/{userId}/sessions/{sessionId}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("#principalId == #userId")
    public void expireUserSession(
            @AuthenticationPrincipal Integer principalId,
            @PathVariable Integer userId,
            @PathVariable Long sessionId
    ) {
        service.expireUserSession(userId, sessionId);
    }


    @Autowired
    public void setService(SessionService service) {
        notNull(service, "SessionService can not be null!");
        this.service = service;
    }

    @Autowired
    public void setService(SessionMapper mapper) {
        notNull(mapper, "SessionMapper can not be null!");
        this.mapper = mapper;
    }
}
