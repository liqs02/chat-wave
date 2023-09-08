package com.chatwave.authservice.domain.dto.response;

public record TokenSetResponse(String refreshToken, String accessToken) {}
