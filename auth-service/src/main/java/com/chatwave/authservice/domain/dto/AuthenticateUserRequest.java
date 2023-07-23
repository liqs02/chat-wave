package com.chatwave.authservice.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AuthenticateUserRequest (
        @NotNull(message = "The id can not be null.")
        Integer id,
        @NotEmpty(message = "The password can not be empty.")
        String password
){}
