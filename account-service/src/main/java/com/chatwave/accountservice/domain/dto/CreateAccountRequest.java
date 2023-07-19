package com.chatwave.accountservice.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record CreateAccountRequest(
        @NotEmpty
        @Length(min = 8, message = "The loginName should contain at least 8 characters.")
        @Length(max = 30, message = "The loginName should contain at most 30 characters.")
        String loginName,
        @NotEmpty
        @Length(min = 3, message = "The displayName should contain at least 3 characters.")
        @Length(max = 30, message = "The displayName should contain at most 30 characters.")
        String displayName,
        @NotEmpty // validated in auth-service
        String password
) {}
