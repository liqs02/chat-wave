package com.chatwave.authservice.domain.dto;

public record TokenSetResponse(String refreshToken, String accessToken) {}
