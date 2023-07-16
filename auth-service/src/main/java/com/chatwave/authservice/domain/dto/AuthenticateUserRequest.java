package com.chatwave.authservice.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AuthenticateUserRequest (
        @NotNull
        Integer id,
        @NotEmpty
        String password
){}
