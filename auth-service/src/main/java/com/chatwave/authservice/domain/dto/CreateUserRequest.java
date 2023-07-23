package com.chatwave.authservice.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record CreateUserRequest(
        @NotNull(message = "The id can not be null.")
        Integer id,
        @NotEmpty(message = "The password can not be empty.")
        @Length(min = 8, message = "The password should contain at least 8 characters.")
        @Length(max = 72, message = "The password should contain at most 72 characters.")
        @Pattern(regexp = "^(?=.*[0-9]).*$", message = "The password should contain at least 1 number.")
        @Pattern(regexp = "^(?=.*[a-z]).*$", message = "The password should contain at least 1 lower case letter.")
        @Pattern(regexp = "^(?=.*[A-Z]).*$", message = "The password should contain at least 1 upper case letter.")
        String password
) {}
