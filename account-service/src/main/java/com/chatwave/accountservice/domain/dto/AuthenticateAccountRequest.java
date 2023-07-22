package com.chatwave.accountservice.domain.dto;

import jakarta.validation.constraints.NotEmpty;

public record AuthenticateAccountRequest(
        @NotEmpty
        String loginName,
        @NotEmpty
        String password
) {}
