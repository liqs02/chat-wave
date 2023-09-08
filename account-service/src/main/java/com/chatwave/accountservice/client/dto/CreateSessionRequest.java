package com.chatwave.accountservice.client.dto;

import jakarta.validation.constraints.NotNull;

public record CreateSessionRequest(Integer userId) {}
