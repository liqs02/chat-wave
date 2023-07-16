package com.chatwave.authservice.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SessionResponse(
   Long id,
   LocalDate expireDate,
   LocalDateTime accessTokenExpireDate,
   LocalDateTime createdAt
) {}
