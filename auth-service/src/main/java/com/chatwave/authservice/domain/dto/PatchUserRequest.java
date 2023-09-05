package com.chatwave.authservice.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record PatchUserRequest(
        @NotEmpty(message = "The password can not be empty.")
        String password,
        @NotEmpty(message = "New password can not be empty.")
        @Length(min = 8, message = "New password should contain at least 8 characters.")
        @Length(max = 72, message = "New password should contain at most 72 characters.")
        @Pattern(regexp = "^(?=.*[0-9]).*$", message = "New password should contain at least 1 number.")
        @Pattern(regexp = "^(?=.*[a-z]).*$", message = "New password should contain at least 1 lower case letter.")
        @Pattern(regexp = "^(?=.*[A-Z]).*$", message = "New password should contain at least 1 upper case letter.")
        String newPassword
) {}
