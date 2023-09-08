package com.chatwave.authservice.domain.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record GetSessionResponse(
   Long id,
   LocalDate expireDate,
   LocalDateTime accessTokenExpireDate,
   LocalDateTime createdAt
) {}
