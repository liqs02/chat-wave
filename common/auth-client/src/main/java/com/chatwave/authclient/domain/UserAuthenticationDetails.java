package com.chatwave.authclient.domain;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

@Getter
public class UserAuthenticationDetails {
    private Long sessionId;
    private String remoteAddress;

    public void setRemoteAddress(HttpServletRequest request) {
        this.remoteAddress = request.getRemoteAddr();
    }
}
