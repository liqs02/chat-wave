package com.chatwave.chatservice.domain.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record GetMessagesRequest(
        @NotNull(message = "The receiverId can not be null")
        Integer receiverId,
        @NotNull(message = "The from value can not be null")
        LocalDateTime from
) {}
