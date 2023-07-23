package com.chatwave.accountservice.domain.dto;

import jakarta.validation.constraints.NotEmpty;

public record PatchPasswordRequest(
        @NotEmpty(message = "The password can not be empty.")
        String password,
        @NotEmpty(message = "New password can not be empty.")
        String newPassword
) {}
