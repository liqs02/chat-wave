package com.chatwave.accountservice.domain.dto;

import org.hibernate.validator.constraints.Length;

public record PatchAccountRequest(
        @Length(min = 3, message = "The displayName should contain at least 3 characters.")
        @Length(max = 30, message = "The displayName should contain at most 30 characters.")
        String displayName,
        String newPassword
) {}
