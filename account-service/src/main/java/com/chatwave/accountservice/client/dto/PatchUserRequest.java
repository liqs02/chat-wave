package com.chatwave.accountservice.client.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record PatchUserRequest(String newPassword) {}
