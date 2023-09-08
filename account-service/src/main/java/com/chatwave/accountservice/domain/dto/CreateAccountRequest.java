package com.chatwave.accountservice.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

// Password is checked in auth-service
public record CreateAccountRequest(
        @NotEmpty(message = "The loginName can not be empty.")
        @Length(min = 8, message = "The loginName should contain at least 8 characters.")
        @Length(max = 30, message = "The loginName should contain at most 30 characters.")
        String loginName,
        @NotEmpty(message = "The displayName can not be empty.")
        String displayName,
        @NotEmpty(message = "The password can not be empty.")
        String password
) {}
