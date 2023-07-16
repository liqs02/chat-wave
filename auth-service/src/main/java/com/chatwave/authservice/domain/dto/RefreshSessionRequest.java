package com.chatwave.authservice.domain.dto;

import jakarta.validation.constraints.NotEmpty;

public record RefreshSessionRequest(
        @NotEmpty
        String refreshToken
){}
