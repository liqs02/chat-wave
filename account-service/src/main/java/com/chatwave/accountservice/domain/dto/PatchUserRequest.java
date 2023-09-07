package com.chatwave.accountservice.domain.dto;

import jakarta.validation.constraints.NotEmpty;

public record PatchUserRequest(
        String password,
        String newPassword
) {}
