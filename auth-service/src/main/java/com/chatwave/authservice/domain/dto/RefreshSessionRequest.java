package com.chatwave.authservice.domain.dto;

import jakarta.validation.constraints.NotEmpty;

public record RefreshSessionRequest(
        @NotEmpty(message = "The refreshToken can not be empty.")
        String refreshToken
){}
