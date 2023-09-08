package com.chatwave.accountservice.client.dto;

import jakarta.validation.constraints.NotEmpty;

public record RefreshSessionRequest(String refreshToken){}
