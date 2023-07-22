package com.chatwave.accountservice.domain.dto;

public record AccountDetails(
        Integer id,
        String loginName,
        String displayName
) {}
