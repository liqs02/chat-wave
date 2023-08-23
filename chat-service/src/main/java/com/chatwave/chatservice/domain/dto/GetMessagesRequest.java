package com.chatwave.chatservice.domain.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record GetMessagesRequest(
        @NotNull(message = "The from value can not be null")
        LocalDateTime from,
        @NotNull(message = "The newer value can not be null")
        Boolean newer
) {}
