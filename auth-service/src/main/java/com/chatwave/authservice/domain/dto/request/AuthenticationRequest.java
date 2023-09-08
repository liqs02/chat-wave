package com.chatwave.authservice.domain.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record AuthenticationRequest(
        @NotEmpty(message = "The loginName can not be empty")
        String loginName,
        @NotEmpty(message = "The password can not be empty")
        String password
){}
