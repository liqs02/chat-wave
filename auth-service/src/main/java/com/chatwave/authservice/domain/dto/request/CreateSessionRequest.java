package com.chatwave.authservice.domain.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateSessionRequest(
        @NotNull(message = "The userId can not be null")
        Integer userId
) {}
