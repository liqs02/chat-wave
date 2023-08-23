package com.chatwave.authclient.domain;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

@Getter
public class UserAuthenticationDetails {
    Long sessionId;
    String remoteAddress;

    public void update(HttpServletRequest request) {
        this.remoteAddress = request.getRemoteAddr();
    }
}
