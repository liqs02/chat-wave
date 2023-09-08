package com.chatwave.authservice.domain.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record RegisterRequest(
        @NotEmpty(message = "The loginName can not be empty.")
        @Length(min = 8, message = "The loginName should contain at least 8 characters.")
        @Length(max = 30, message = "The loginName should contain at most 30 characters.")
        String loginName,
        @NotEmpty(message = "The password can not be empty")
        @Length(min = 8, message = "The password should contain at least 8 characters")
        @Length(max = 72, message = "The password should contain at most 72 characters")
        @Pattern(regexp = "^(?=.*[0-9]).*$", message = "The password should contain at least 1 number")
        @Pattern(regexp = "^(?=.*[a-z]).*$", message = "The password should contain at least 1 lower case letter")
        @Pattern(regexp = "^(?=.*[A-Z]).*$", message = "The password should contain at least 1 upper case letter")
        String password
) {}
