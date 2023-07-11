package com.chatwave.authservice.domain.dto;

import com.chatwave.authservice.domain.RefreshToken;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserTokenSet {
    private UUID refreshToken;
    private String accessToken;

    public UserTokenSet(RefreshToken refreshToken, String accessToken) {
        this.refreshToken = refreshToken.getId();
        this.accessToken = accessToken;
    }
}
