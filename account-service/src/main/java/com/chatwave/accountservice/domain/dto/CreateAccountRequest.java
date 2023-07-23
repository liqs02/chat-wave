package com.chatwave.accountservice.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record CreateAccountRequest(
        @NotEmpty(message = "The loginName can not be empty.")
        @Length(min = 8, message = "The loginName should contain at least 8 characters.")
        @Length(max = 30, message = "The loginName should contain at most 30 characters.")
        String loginName,
        @NotEmpty(message = "The displayName can not be empty.")
        @Length(min = 3, message = "The displayName should contain at least 3 characters.")
        @Length(max = 30, message = "The displayName should contain at most 30 characters.")
        String displayName,
        @NotEmpty(message = "The password can not be empty.")
        // Password is checked in auth-service
        String password
) {}
